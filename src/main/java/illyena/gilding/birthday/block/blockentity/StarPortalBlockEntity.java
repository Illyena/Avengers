package illyena.gilding.birthday.block.blockentity;

import com.mojang.datafixers.util.Pair;
import illyena.gilding.birthday.block.BirthdayBlocks;
import illyena.gilding.birthday.block.StarPortalBlock;
import illyena.gilding.birthday.structure.StarLabStructure;
import illyena.gilding.core.util.GildingTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
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
import java.util.Objects;
import java.util.Optional;

import static illyena.gilding.birthday.BirthdayInitializer.LOGGER;

public class StarPortalBlockEntity extends BlockEntity {
    public long age;
    private int teleportCooldown;
    private final int requiredPlayerRange = 4;
    private int viewerCount;
    public AnimationStage animationStage;
    private float animationProgress;
    private float prevAnimationProgress;
    private float pulseProgress;
    private float prevPulseProgress;
    @Nullable
    private final DyeColor cachedColor;
    @Nullable
    private BlockPos exitPortalPos;
    private boolean exactTeleport;

    public StarPortalBlockEntity(@Nullable DyeColor color, BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.animationStage = StarPortalBlockEntity.AnimationStage.CLOSED;
        this.cachedColor = color;
    }

    public StarPortalBlockEntity(BlockPos pos, BlockState state) {
        this(null, BirthdayBlockEntities.STAR_PORTAL_BLOCK_ENTITY ,pos, state);
    }

    public Box getBoundingBox(BlockState state) {
        return ShulkerEntity.calculateBoundingBox((Direction)state.get(StarPortalBlock.FACING), 0.5F * this.getAnimationProgress(1.0F));
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

/*    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("Age", this.age);
        if (this.exitPortalPos != null) {
            nbt.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortalPos));
        }

        if (this.exactTeleport) {
            nbt.putBoolean("ExactTeleport", true);
        }

    }
*/
/*
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.age = nbt.getLong("Age");
        if (nbt.contains("ExitPortal", 10)) {
            BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("ExitPortal"));
            if (World.isValid(blockPos)) {
                this.exitPortalPos = blockPos;
            }
        }

        this.exactTeleport = nbt.getBoolean("ExactTeleport");
    }
*/

    public static void tick(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        StarPortalBlock block = (StarPortalBlock)state.getBlock();
        if (blockEntity.isPlayerInRange(world, pos) && StarPortalBlock.canOpen(state, world, pos, blockEntity)) {
            blockEntity.animationStage = AnimationStage.OPENING;
        } else { blockEntity.animationStage = AnimationStage.CLOSING; }

        blockEntity.updateAnimation(world, pos, state);
        blockEntity.updatePulse(world, pos, state);

        if (blockEntity.needsCooldownBeforeTeleporting()) {
            --blockEntity.teleportCooldown;
        }
    }

/*
    public static void clientTick(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        blockEntity.updateAnimation(world, pos, state);
        blockEntity.updatePulse(world, pos, state);
        ++blockEntity.age;
        if (blockEntity.needsCooldownBeforeTeleporting()) {
            --blockEntity.teleportCooldown;
        }

    }

    public static void serverTick(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        boolean bl = blockEntity.isRecentlyGenerated();
        boolean bl2 = blockEntity.needsCooldownBeforeTeleporting();
        ++blockEntity.age;
        if (bl2) {
            --blockEntity.teleportCooldown;
        } else {
            List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(pos), StarPortalBlockEntity::canTeleport);
            if (!list.isEmpty()) {
                tryTeleportingEntity(world, pos, state, (Entity)list.get(world.random.nextInt(list.size())), blockEntity);
            }

            if (blockEntity.age % 2400L == 0L) {
                startTeleportCooldown(world, pos, state, blockEntity);
            }
        }

        if (bl != blockEntity.isRecentlyGenerated() || bl2 != blockEntity.needsCooldownBeforeTeleporting()) {
            markDirty(world, pos, state);
        }

    }
*/

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
                }
                this.pushEntities(world, pos, state);
            }
            case CLOSING -> {
                this.animationProgress -= 0.1F;
                if (this.animationProgress <= 0.0F) {
                    this.animationStage = AnimationStage.CLOSED;
                    this.animationProgress = 0.0F;
                    updateNeighborStates(world, pos, state);
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
        return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() +0.5, (double)pos.getZ() +0.5, (double)this.requiredPlayerRange);
    }

    private void pushEntities(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StarPortalBlock) {
            Direction direction = state.get(StarPortalBlock.FACING);
            Box box = ShulkerEntity.calculateBoundingBox(direction, this.prevAnimationProgress, this.animationProgress).offset(pos);
            List<Entity> list = world.getOtherEntities((Entity)null, box);
            if (!list.isEmpty()) {
                for (Entity entity : list) {
                    if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                        entity.move(MovementType.SHULKER_BOX, new Vec3d((box.getXLength() + 0.01) * (double) direction.getOffsetX(), (box.getYLength() + 0.01) * (double) direction.getOffsetY(), (box.getZLength() + 0.01) * (double) direction.getOffsetZ()));
                    }
                }

            }
        }
    }

    public boolean suffocates() { return this.animationStage == AnimationStage.CLOSED; }

    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, this.pos);
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);

        }

    } // todo

    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, this.pos);
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);

        }
    } //todo

    public BlockEntityUpdateS2CPacket toUpdatePacket() { return  BlockEntityUpdateS2CPacket.create(this); }

    public NbtCompound toInitialChunkDataNbt() { return this.createNbt(); }

    /** teleporting */
    public static boolean canTeleport(Entity entity) {
        return EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !entity.getRootVehicle().hasPortalCooldownn();
    }

    public boolean needsCooldownBeforeTeleporting() {
        return this.teleportCooldown > 0;
    }

    private static void startTeleportCooldown(World world, BlockPos pos, BlockState state, StarPortalBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.teleportCooldown = 40;
            world.addSyncedBlockEvent(pos, state.getBlock(), 2, 0);
            markDirty(world, pos, state);
        }
    }

    public static void tryTeleportingEntity(World world, BlockPos pos, BlockState state , Entity entity, StarPortalBlockEntity blockEntity) {
        if (world instanceof ServerWorld serverWorld && !blockEntity.needsCooldownBeforeTeleporting()) {
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
        TagKey<Structure> tag = GildingTags.GildingStructureTags.STAR_PORTAL_TELEPORTS_TO;

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

        Optional<RegistryEntryList.Named<Structure>> optional = world.getRegistryManager().get(Registry.STRUCTURE_KEY).getEntryList(GildingTags.GildingStructureTags.STAR_PORTAL_TELEPORTS_TO);
        if (optional.isPresent()) {
            Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, optional.get(), structurePos, 100, false);
            if (pair != null && pair.getSecond().value() instanceof StarLabStructure labStructure) {
                List<StructureStart> list = world.getStructureAccessor().getStructureStarts(world.getChunk(structurePos).getPos(), structure -> structure instanceof StarLabStructure);
                if (!list.isEmpty()) {
                    List<StructurePiece> pieces = list.get(0).getChildren();
/*                   pieces.forEach((instance) -> {
                       BlockPos blockPos = getBlockInBox(world, Box.from(instance.getBoundingBox()), BirthdayBlocks.TELEPORT_POINT);
                       if (blockPos != null) {
                           BirthdayInitializer.LOGGER.debug("TeleportAnchor found at {}", blockPos);
                           teleportAnchor.set(blockPos);
                       }
                   });
 */
                    for (StructurePiece piece : pieces) {
                        BlockPos blockPos = getBlockInBox(world, Box.from(piece.getBoundingBox()), BirthdayBlocks.TELEPORT_ANCHOR);
                        if (blockPos != null) {
                            LOGGER.debug("TeleportAnchor found at {}", blockPos);
                            teleportAnchor = blockPos;
                            break;
                        }
                    }
                }
            }
        }
        return teleportAnchor; // != null ? teleportAnchor : pos;
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
//                    for (int k = world.getTopY() - 1; k > (blockPos == null ? world.getBottomY() : blockPos.getY()); --k) {
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

        return  blockPos; // == null ? pos : blockPos;
    }

    private static void createPlatform(ServerWorld world, BlockPos pos) {
        BlockPos blockPos = new BlockPos(pos.down());
        ChunkGenerator generator = world.getChunkManager().getChunkGenerator();

        Optional<RegistryEntryList.Named<Structure>> optional = world.getRegistryManager().get(Registry.STRUCTURE_KEY).getEntryList(GildingTags.GildingStructureTags.STAR_PORTAL_TELEPORTS_TO);
        if (optional.isPresent()) {
            Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, optional.get(), pos, 100, false);
            if (pair != null && pair.getSecond().value() instanceof StarLabStructure labStructure) {
                List<StructureStart> list = world.getStructureAccessor().getStructureStarts(world.getChunk(pos).getPos(), structure -> structure instanceof StarLabStructure);
                if (!list.isEmpty()) {
                    List<StructurePiece> pieces = list.get(0).getChildren();
                    BlockBox box = pieces.get(0).getBoundingBox();
                    pieces.get(0).generate(world, world.getStructureAccessor(), generator, world.getRandom(), box, world.getChunk(pos).getPos(), blockPos );
                }
            }
        }
    }

/*
    private static BlockPos findBestPortalExitPos(World world, BlockPos pos) {
        BlockPos blockPos = findExitPortalPos(world, pos.add(0, 2, 0), 5, false);
        LOGGER.debug("Best exit position for portal at {} is {}", pos, blockPos);
        return blockPos.up();
    }

    private static BlockPos setupExitPortalLocation(ServerWorld world, BlockPos pos) {
        Vec3d vec3d = findTeleportLocation(world, pos);
        WorldChunk worldChunk = getChunk(world, vec3d);
        BlockPos blockPos = findPortalPosition(worldChunk);
        if (blockPos == null) {
            blockPos = new BlockPos(vec3d.x + 0.5, 75.0, vec3d.z + 0.5);
            LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockPos);
            ((ConfiguredFeature) EndConfiguredFeatures.END_ISLAND.value()).generate(world, world.getChunkManager().getChunkGenerator(), Random.create(blockPos.asLong()), blockPos);
        } else {
            LOGGER.debug("Found suitable block to teleport to: {}", blockPos);
        }

        blockPos = findExitPortalPos(world, blockPos, 16, true);
        return blockPos;
    }

    private static BlockPos findExitPortalPos(BlockView world, BlockPos pos, int searchRadius, boolean force) {
        BlockPos blockPos = null;

        for(int i = -searchRadius; i <= searchRadius; ++i) {
            for(int j = -searchRadius; j <= searchRadius; ++j) {
                if (i != 0 || j != 0 || force) {
                    for(int k = world.getTopY() - 1; k > (blockPos == null ? world.getBottomY() : blockPos.getY()); --k) {
                        BlockPos blockPos2 = new BlockPos(pos.getX() + i, k, pos.getZ() + j);
                        BlockState blockState = world.getBlockState(blockPos2);
                        if (blockState.isFullCube(world, blockPos2) && (force || !blockState.isOf(Blocks.BEDROCK))) {
                            blockPos = blockPos2;
                            break;
                        }
                    }
                }
            }
        }

        return blockPos == null ? pos : blockPos;
    }

    @Nullable
    private static BlockPos findPortalPosition(WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 30, chunkPos.getStartZ());
        int i = chunk.getHighestNonEmptySectionYOffset() + 16 - 1;
        BlockPos blockPos2 = new BlockPos(chunkPos.getEndX(), i, chunkPos.getEndZ());
        BlockPos blockPos3 = null;
        double d = 0.0;
        Iterator var8 = BlockPos.iterate(blockPos, blockPos2).iterator();

        while(true) {
            BlockPos blockPos4;
            double e;
            do {
                BlockPos blockPos5;
                BlockPos blockPos6;
                do {
                    BlockState blockState;
                    do {
                        do {
                            if (!var8.hasNext()) {
                                return blockPos3;
                            }

                            blockPos4 = (BlockPos)var8.next();
                            blockState = chunk.getBlockState(blockPos4);
                            blockPos5 = blockPos4.up();
                            blockPos6 = blockPos4.up(2);
                        } while(!blockState.isOf(Blocks.END_STONE));
                    } while(chunk.getBlockState(blockPos5).isFullCube(chunk, blockPos5));
                } while(chunk.getBlockState(blockPos6).isFullCube(chunk, blockPos6));

                e = blockPos4.getSquaredDistanceFromCenter(0.0, 0.0, 0.0);
            } while(blockPos3 != null && !(e < d));

            blockPos3 = blockPos4;
            d = e;
        }
    }

    private static boolean isChunkEmpty(ServerWorld world, Vec3d pos) {
        return getChunk(world, pos).getHighestNonEmptySectionYOffset() <= world.getBottomY();
    }

    private static WorldChunk getChunk(World world, Vec3d pos) {
        return world.getChunk(MathHelper.floor(pos.x / 16.0), MathHelper.floor(pos.z / 16.0));
    }
*/

    private static boolean isAirOrFluid(BlockState blockState) {
        return blockState.isAir() || blockState.getBlock() instanceof FluidBlock;
    }

    public void setExitPortalPos(BlockPos pos, boolean exactTeleport) {
        this.exactTeleport = exactTeleport;
        this.exitPortalPos = pos;
    }

    public boolean onSyncedBlockEvent(int type, int data) {
/*        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = AnimationStage.CLOSING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            if (data == 1) {
                this.animationStage = AnimationStage.OPENING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
 */

        if (type == 2) {
            this.teleportCooldown = 40;
            return  true;
        } else return super.onSyncedBlockEvent(type, data);
    } //todo

/*
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.teleportCooldown = 40;
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = StarPortalBlockEntity.AnimationStage.CLOSING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            if (data == 1) {
                this.animationStage = StarPortalBlockEntity.AnimationStage.OPENING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }
*/



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
        Direction[] var2 = Direction.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction direction = var2[var4];
            i += this.shouldDrawSide(direction) ? 1 : 0;
        }

        return i;
    }


    public static enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

        private AnimationStage() { }
    }

}
