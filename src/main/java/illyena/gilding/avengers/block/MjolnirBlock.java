package illyena.gilding.avengers.block;

import illyena.gilding.avengers.advancement.AvengersAdvancements;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.item.custom.MjolnirItem;
import illyena.gilding.core.block.util.LimitedFallingBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static illyena.gilding.avengers.AvengersInit.LOGGER;
import static illyena.gilding.avengers.AvengersInit.translationKeyOf;
import static illyena.gilding.avengers.config.AvengersConfigOptions.MJOLNIR_LEGACY;

public class MjolnirBlock extends BlockWithEntity implements LandingBlock, LimitedFallingBlock, Waterloggable {
    public static final BooleanProperty LEGACY = BooleanProperty.of("legacy");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");

    private FallingBlockEntity fallingBlockEntity;

    private static VoxelShape composeLegacyNSShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.34375, 0.0, 0.40625, 0.65625, 0.1875, 0.59375));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.46875, 0.0, 0.46875, 0.53125, 0.625, 0.53125));
        return shape;
    }

    private static VoxelShape composeLegacyEWShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.40625, 0.0, 0.34375, 0.59375 , 0.1875, 0.65625));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.46875, 0.0, 0.46875, 0.53125, 0.625, 0.53125));
        return shape;
    }

    private static VoxelShape composeNSShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.375, 0.0, 0.3125, 0.625, 0.25, 0.6875));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375, 0.0, 0.4375, 0.5625, 0.6875, 0.5625));
        return shape;
    }

    private static VoxelShape composeEWShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.3125, 0.0, 0.375, 0.6875, 0.25, 0.625));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375, 0.0, 0.4375, 0.5625, 0.6875, 0.5625));
        return shape;
    }

    public MjolnirBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LEGACY, MJOLNIR_LEGACY.getValue())
                .with(FACING, Direction.NORTH)
                .with(BROKEN, false)
                .with(Properties.WATERLOGGED, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (MJOLNIR_LEGACY.getValue()) {
            return state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH ? composeLegacyNSShape() : composeLegacyEWShape();
        }
        return state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH ? composeNSShape() : composeEWShape();
    }

    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else if (!world.isClient) {
            ItemStack itemStack = new ItemStack(this.asItem());
            world.getBlockEntity(pos, AvengersBlockEntities.MJOLNIR_BLOCK_ENTITY).ifPresent(mjolnirBlockEntity -> {
                NbtCompound nbtCompound = new NbtCompound();
                if (mjolnirBlockEntity.getEnchantments() != null) {
                    nbtCompound.put("Enchantments", mjolnirBlockEntity.getEnchantments());
                }
                itemStack.setNbt(nbtCompound);
                itemStack.setCustomName(mjolnirBlockEntity.getCustomName());
                itemStack.setDamage(mjolnirBlockEntity.getDamage());
            });
            if (this.asItem() instanceof MjolnirItem mjolnirItem && mjolnirItem.isWorthy(player) || player.isCreative()) {
                player.getInventory().insertStack(itemStack);
                world.removeBlock(pos, false);
                return ActionResult.SUCCESS;
            } else {
                if (!MJOLNIR_LEGACY.getValue()){
                    player.addExhaustion(player.isSneaking() ? 1.5f : 1.0f);
                }
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    AvengersAdvancements.NOT_WORTHY.trigger(serverPlayer, itemStack, pos);
                }
                return ActionResult.FAIL;
            }
        }
        if (MJOLNIR_LEGACY.getValue() && this.asItem() instanceof MjolnirItem mjolnirItem && !mjolnirItem.isWorthy(player) && world.isClient() && hand == player.getActiveHand()) {
            player.sendMessage(translationKeyOf("message", "not_worthy"));
        }
        return ActionResult.PASS;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState().with(LEGACY, MJOLNIR_LEGACY.getValue()).with(FACING, context.getHorizontalPlayerFacing()).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack itemStack) {
        world.getBlockEntity(pos, AvengersBlockEntities.MJOLNIR_BLOCK_ENTITY).ifPresent(blockEntity -> blockEntity.readFrom(itemStack));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, this.getFallDelay());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        world.scheduleBlockTick(pos, this, this.getFallDelay());
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() > world.getBottomY()) {
            this.fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            this.configureFallingBlockEntity(this.fallingBlockEntity);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEGACY, FACING, BROKEN, Properties.WATERLOGGED);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        LimitedFallingBlock.super.randomDisplayTick(state, world, pos, random);
    }


    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new MjolnirBlockEntity(pos, state); }

    /** Landing Block */
    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1031, pos, 0);
        }
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1029, pos, 0);
        }
    }

    @Override
    public DamageSource getDamageSource(Entity attacker) { return attacker.getDamageSources().fallingBlock(attacker); }


    /** LimitedFallingBlock */
    @Override
    public void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity) {
        fallingBlockEntity.setHurtEntities(2.0f, 40);
    }

    @Override
    public int getFallDelay() { return 2; }

    @Override
    public boolean limit() {
        return this.fallingBlockEntity != null &&
                this.fallingBlockEntity.getBlockPos().getY() <= this.fallingBlockEntity.getWorld().getBottomY();
    }

    @Override
    public void action() {
        if (!this.fallingBlockEntity.getWorld().isClient()) {
            World world = this.fallingBlockEntity.getWorld();
            BlockState state = this.fallingBlockEntity.getBlockState();
            Block block = state.getBlock();
            BlockPos blockPos = this.fallingBlockEntity.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            this.fallingBlockEntity.setVelocity(this.fallingBlockEntity.getVelocity().multiply(0.7, -0.5, 0.7));
            if (!blockState.isOf(Blocks.MOVING_PISTON)) {

                boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                boolean bl5 = state.canPlaceAt(world, blockPos);
                if (bl3 && bl5) {
                    if (state.contains(Properties.WATERLOGGED) && world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
                        state = state.with(Properties.WATERLOGGED, true);
                    }

                    if (world.setBlockState(blockPos, state, 3)) {
                        ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.sendToOtherNearbyPlayers(this.fallingBlockEntity, new BlockUpdateS2CPacket(blockPos, world.getBlockState(blockPos)));
                        this.fallingBlockEntity.discard();
                        if (block instanceof LandingBlock) {
                            ((LandingBlock) block).onLanding(world, blockPos, state, blockState, this.fallingBlockEntity);
                        }

                        if (this.fallingBlockEntity.blockEntityData != null && state.hasBlockEntity()) {
                            BlockEntity blockEntity = world.getBlockEntity(blockPos);
                            if (blockEntity != null) {
                                NbtCompound nbtCompound = blockEntity.createNbt();

                                for (String string : this.fallingBlockEntity.blockEntityData.getKeys()) {
                                    nbtCompound.put(string, this.fallingBlockEntity.blockEntityData.get(string).copy());
                                }

                                try {
                                    blockEntity.readNbt(nbtCompound);
                                } catch (Exception var15) {
                                    LOGGER.error("Failed to load block entity from falling block", var15);
                                }

                                blockEntity.markDirty();
                            }
                        }
                    } else if (this.fallingBlockEntity.dropItem && this.fallingBlockEntity.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.fallingBlockEntity.discard();
                        this.fallingBlockEntity.onDestroyedOnLanding(block, blockPos);
                        this.fallingBlockEntity.dropItem(block);
                    }
                } else {
                    this.fallingBlockEntity.discard();
                    if (this.fallingBlockEntity.dropItem && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.fallingBlockEntity.onDestroyedOnLanding(block, blockPos);
                        this.fallingBlockEntity.dropItem(block);
                    }
                }

            }
        }

    }

    public boolean canFallThrough(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isReplaceable();//|| state.isLiquid()
    }


}