package illyena.gilding.avengers.block.blockentity;

import com.mojang.datafixers.util.Pair;
import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.StarPortalBlock;
import illyena.gilding.avengers.structure.StarLabStructure;
import illyena.gilding.avengers.util.data.AvengersStructureTagGenerator;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static illyena.gilding.avengers.AvengersInit.LOGGER;

public class StarPortalBlockEntity extends BlockEntity {
    private int teleportCooldown;
    private final int requiredPlayerRange = 4;
    public AnimationStage animationStage;
    private float animationProgress;
    private float prevAnimationProgress;
    private float pulseProgress;
    private float prevPulseProgress;
    @Nullable
    public DyeColor cachedColor;
    @Nullable
    private BlockPos exitPortalPos;
    private boolean exactTeleport;

    public StarPortalBlockEntity(@Nullable DyeColor color, BlockPos pos, BlockState state) {
        super(AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, pos, state);
        this.animationStage = StarPortalBlockEntity.AnimationStage.CLOSED;
        this.cachedColor = color;
    }

    public StarPortalBlockEntity(BlockPos pos, BlockState state) { this(null, pos, state); }

    public Box getBoundingBox(BlockState state) {
        return ShulkerEntity.calculateBoundingBox(state.get(StarPortalBlock.FACING), 0.5F * this.getAnimationProgress(1.0F));
    }

    public Box getHeadBoundingBox(BlockState state) {
        Direction direction = state.get(StarPortalBlock.FACING);
        float g = 0.0325f *( MathHelper.sin(getPulseProgress(this.pulseProgress - this.prevPulseProgress) / 6.0f)) ;  // sine from renderer, amplitude adjusted manually to match, tickDelta pulled from pulseProgress delta
        Vec3d vec3d = new Box(BlockPos.ORIGIN).getCenter();
        float offset = 0.125f;                                                                                                   // inverse of y offset from model
        Box box = new Box(vec3d.getX() - 0.3125f - g, vec3d.getY() - 0.3125f - g, vec3d.getZ() - 0.3125f - g,         // 0.3125f is 5/16 and half of the 10 x 10 cube is added to each direction from center
                vec3d.getX() + 0.3125f + g, vec3d.getY() + 0.3125f + g, vec3d.getZ() + 0.3125f + g);

        return box.offset(direction.getOffsetX() * offset , direction.getOffsetY() * offset, direction.getOffsetZ() * offset);
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.exitPortalPos != null) {
            nbt.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortalPos));
        }

        if (this.exactTeleport) {
            nbt.putBoolean("ExactTeleport", true);
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("ExitPortal", 10)) {
            BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("ExitPortal"));
            if (World.isValid(blockPos)) {
                this.exitPortalPos = blockPos;
            }
        }
        this.exactTeleport = nbt.getBoolean("ExactTeleport");
    }

    public static void tick(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        if (blockEntity.isPlayerInRange(world, pos) && StarPortalBlock.canOpen(state, world, pos, blockEntity)) {
            if (blockEntity.getAnimationStage() != AnimationStage.OPENED) {
                blockEntity.animationStage = AnimationStage.OPENING;
            }
        } else if (blockEntity.getAnimationStage() != AnimationStage.CLOSED){
            blockEntity.animationStage = AnimationStage.CLOSING;
        }
        blockEntity.updateAnimation(world, pos, state);
        blockEntity.updatePulse(world, pos, state);
        if (blockEntity.needsCooldownBeforeTeleporting()) {
            --blockEntity.teleportCooldown;
        }
    }

    private void updatePulse(World world, BlockPos pos, BlockState state) {
        this.prevPulseProgress = this.pulseProgress;
        switch (this.animationStage) {
            case CLOSED -> this.pulseProgress = 0.0f;
            case OPENED -> this.pulseProgress += 0.1f;
        }
    }

    private void updateAnimation(World world, BlockPos pos, BlockState state) {
        this.prevAnimationProgress = this.animationProgress;
        switch (this.animationStage) {
            case CLOSED -> this.animationProgress = 0.0F;
            case OPENING -> {
                this.animationProgress += 0.1F;
                if (this.animationProgress >= 1.0F) {
                    this.animationStage = AnimationStage.OPENED;
                    this.animationProgress = 1.0F;
                    updateNeighborStates(world, pos, state);
                    onOpen(world, pos);
                }
                this.pushEntities(world, pos, state);
            }
            case CLOSING -> {
                this.animationProgress -= 0.1F;
                if (this.animationProgress <= 0.0F) {
                    this.animationStage = AnimationStage.CLOSED;
                    this.animationProgress = 0.0F;
                    updateNeighborStates(world, pos, state);
                    onClose(world, pos);
                }
            }
            case OPENED -> this.animationProgress = 1.0F;
        }

    }

    public AnimationStage getAnimationStage() { return this.animationStage; }

    private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
        state.updateNeighbors(world, pos, 3);
    }

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() +0.5, (double)pos.getZ() +0.5, this.requiredPlayerRange);
    }

    private void pushEntities(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StarPortalBlock) {
            Direction direction = state.get(StarPortalBlock.FACING);
            Box box = ShulkerEntity.calculateBoundingBox(direction, this.prevAnimationProgress, this.animationProgress).offset(pos);
            List<Entity> list = world.getOtherEntities(null, box);
            if (!list.isEmpty()) {
                for (Entity entity : list) {
                    if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                        entity.move(MovementType.SHULKER_BOX, new Vec3d((box.getXLength() + 0.0) * (double) direction.getOffsetX(), (box.getYLength() + 0.0) * (double) direction.getOffsetY(), (box.getZLength() + 0.0) * (double) direction.getOffsetZ()));
                    }
                }

            }
        }
    }

    public boolean suffocates() { return this.animationStage == AnimationStage.CLOSED; }

    public void onOpen(World world, BlockPos pos) {
        PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), requiredPlayerRange * 2, false);
        if (player != null && !player.isSpectator()) {
            world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, pos);
            world.playSound(null, pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            PiglinBrain.onGuardedBlockInteracted(player, true);
        }

    }

    public void onClose(World world, BlockPos pos) {
        PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), requiredPlayerRange * 2, false);
        if (player != null && !player.isSpectator()) {
            world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, pos);
            world.playSound(null, pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() { return  BlockEntityUpdateS2CPacket.create(this); }

    public NbtCompound toInitialChunkDataNbt() { return this.createNbt(); }

    /** teleporting */
    public static boolean canTeleport(Entity entity) {
        return EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !entity.getRootVehicle().hasPortalCooldown();
    }

    public boolean needsCooldownBeforeTeleporting() { return this.teleportCooldown > 0; }

    private static void startTeleportCooldown(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.teleportCooldown = 40;
            world.addSyncedBlockEvent(pos, state.getBlock(), 1, 0);
            markDirty(world, pos, state);
        }
    }

    public static void tryTeleportingEntity(World world, BlockPos pos, BlockState state , Entity entity, StarPortalBlockEntity blockEntity) {
        if (world instanceof ServerWorld serverWorld && !blockEntity.needsCooldownBeforeTeleporting() && canTeleport(entity)) {
            blockEntity.teleportCooldown = 100;
            BlockPos teleportPoint;
            if (blockEntity.exitPortalPos == null && world.getRegistryKey() == World.END) {
                teleportPoint = findTeleportLocation(serverWorld, pos);
                blockEntity.exitPortalPos = teleportPoint;
            }

            if (blockEntity.exitPortalPos != null) {

                teleportPoint = blockEntity.exactTeleport ? blockEntity.exitPortalPos : findBestPortalExitPos(serverWorld, blockEntity.exitPortalPos);
                Entity entity3 = null;
                if (entity instanceof EnderPearlEntity) {
                    Entity entity2 = ((EnderPearlEntity)entity).getOwner();
                    if (entity2 instanceof ServerPlayerEntity) {
                        Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)entity2, state);
                    }
                    if (entity2 != null) {
                        entity3 = entity2;
                        entity.discard();
                    }
                } else {
                    entity3 = entity.getRootVehicle();
                }
                entity3.resetPortalCooldown();
                entity3.teleport((double)teleportPoint.getX() + 0.5, (double)teleportPoint.getY() + 1, (double)teleportPoint.getZ() + 0.5);
                world.emitGameEvent(entity3, GameEvent.TELEPORT, teleportPoint);
            }

            startTeleportCooldown(world, pos, state, blockEntity);
        }
    }

    public static BlockPos findTeleportLocation(ServerWorld world, BlockPos pos) {
        BlockPos teleportPoint = pos;
        TagKey<Structure> tag = AvengersStructureTagGenerator.STAR_PORTAL_TELEPORTS_TO;

        BlockPos structurePos = world.locateStructure(tag, pos, 100, false);
        if (structurePos == null) {
            structurePos = world.locateStructure(tag, BlockPos.ORIGIN, 100, false);
        }
        if (structurePos != null) {
            teleportPoint = getTeleportAnchor(world, structurePos);
        }
        return teleportPoint != null ? teleportPoint : pos;
    }

    private static BlockPos getTeleportAnchor(ServerWorld world, BlockPos structurePos) {
        BlockPos teleportAnchor = null;

        Optional<RegistryEntryList.Named<Structure>> optional = world.getRegistryManager().get(Registry.STRUCTURE_KEY).getEntryList(AvengersStructureTagGenerator.STAR_PORTAL_TELEPORTS_TO);
        if (optional.isPresent()) {
            Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, optional.get(), structurePos, 100, false);
            if (pair != null && pair.getSecond().value() instanceof StarLabStructure) {
                List<StructureStart> list = world.getStructureAccessor().getStructureStarts(world.getChunk(structurePos).getPos(), structure -> structure instanceof StarLabStructure);
                if (!list.isEmpty()) {
                    List<StructurePiece> pieces = list.get(0).getChildren();
                    for (StructurePiece piece : pieces) {
                        BlockPos blockPos = getBlockInBox(world, Box.from(piece.getBoundingBox()), AvengersBlocks.TELEPORT_ANCHOR);
                        if (blockPos != null) {
                            LOGGER.debug("TeleportAnchor found at {}", blockPos);
                            teleportAnchor = blockPos;
                            break;
                        }
                    }
                }
            }
        }
        return teleportAnchor;
    }

    private static BlockPos getBlockInBox(World world, Box box, Block block) {
        BlockPos blockPos1 = new BlockPos(box.minX, box.minY, box.minZ);
        BlockPos blockPos2 = new BlockPos(box.maxX, box.maxY, box.maxZ);
        Iterator<BlockPos> iterator = BlockPos.iterate(blockPos1, blockPos2).iterator();
        BlockPos blockPos3 = null;

        while (iterator.hasNext()) {
            BlockPos blockPos4 = iterator.next();
            BlockState state = world.getBlockState(blockPos4);
            if (state.isOf(block)) {
                blockPos3 = blockPos4;
                break;
            }
        }

        return blockPos3;
    }

    private static BlockPos findBestPortalExitPos(ServerWorld world, BlockPos teleportPoint) {
        BlockPos blockPos = findFullCubeNear(world, teleportPoint, 3, false);
        LOGGER.debug("Best exit position for portal at {} is {}", teleportPoint, blockPos);
        if (blockPos == null) {
            LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", teleportPoint.down());
            createPlatform(world, teleportPoint);
            blockPos = findFullCubeNear(world, teleportPoint, 5, false);
        }

        return blockPos;
    }

    private static BlockPos findFullCubeNear(BlockView world, BlockPos pos, int searchRadius, boolean force) {
        BlockPos blockPos = null;
        for (int i = -searchRadius; i <= searchRadius; ++i) {
            for (int j = -searchRadius; j <= searchRadius; ++j) {
                if (i != 0 || j != 0 || force) {
                    for (int k = -searchRadius; k <= searchRadius; ++k) {
                        BlockPos blockPos2 = new BlockPos(pos.getX() + i, pos.getY() + k, pos.getZ() + j);
                        BlockState blockState = world.getBlockState(blockPos2);
                        BlockState blockState1 = world.getBlockState(blockPos2.up(1));
                        BlockState blockState2 = world.getBlockState(blockPos2.up(2));
                        if (blockState.isFullCube(world, blockPos2) && isAirOrFluid(blockState1) && isAirOrFluid(blockState2) && (force || !blockState.isOf(Blocks.BEDROCK))) {
                            blockPos = blockPos2;
                            break;
                        }
                    }
                }
            }
        }

        return  blockPos;
    }

    private static void createPlatform(ServerWorld world, BlockPos pos) {
        BlockPos blockPos = new BlockPos(pos.down());
        ChunkGenerator generator = world.getChunkManager().getChunkGenerator();

        Optional<RegistryEntryList.Named<Structure>> optional = world.getRegistryManager().get(Registry.STRUCTURE_KEY).getEntryList(AvengersStructureTagGenerator.STAR_PORTAL_TELEPORTS_TO);
        if (optional.isPresent()) {
            Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, optional.get(), pos, 100, false);
            if (pair != null && pair.getSecond().value() instanceof StarLabStructure) {
                List<StructureStart> list = world.getStructureAccessor().getStructureStarts(world.getChunk(pos).getPos(), structure -> structure instanceof StarLabStructure);
                if (!list.isEmpty()) {
                    List<StructurePiece> pieces = list.get(0).getChildren();
                    BlockBox box = pieces.get(0).getBoundingBox();
                    pieces.get(0).generate(world, world.getStructureAccessor(), generator, world.getRandom(), box, world.getChunk(pos).getPos(), blockPos );
                }
            }
        }
    }

    private static boolean isAirOrFluid(BlockState blockState) {
        return blockState.isAir() || blockState.getBlock() instanceof FluidBlock;
    }

    @Nullable
    public  BlockPos getExitPortalPos() { return this.exitPortalPos; }

    public boolean getExactTeleport() { return this.exactTeleport; }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.teleportCooldown = 40;
            return  true;
        } else return super.onSyncedBlockEvent(type, data);
    }


    /** for rendering */

    public float getAnimationProgress(float delta) {
        return MathHelper.lerp(delta, this.prevAnimationProgress, this.animationProgress);
    }

    public float getPulseProgress(float delta) {
        return MathHelper.lerp(delta, this.prevPulseProgress, this.pulseProgress);
    }

    @Nullable
    public DyeColor getColor() { return this.cachedColor; }

    public boolean shouldDrawSide(Direction direction) {
        return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction, this.getPos().offset(direction));
    }

    public int getDrawnSidesCount() {
        int i = 0;
        for (Direction direction : Direction.values()) {
            i += this.shouldDrawSide(direction) ? 1 : 0;
        }
        return i;
    }

    public enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

        AnimationStage() { }
    }

}
