package illyena.gilding.avengers.block;

import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.block.blockentity.TeleportAnchorBlockEntity;
import illyena.gilding.core.block.util.FluidFlowsThrough;
import illyena.gilding.core.particle.GildingParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class TeleportAnchorBlock extends BlockWithEntity implements BlockEntityProvider, FluidFlowsThrough {
    public TeleportAnchorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(WATER_LEVEL, 0).with(FLUID_FALL, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (context.isHolding(AvengersBlocks.TELEPORT_ANCHOR.asItem()) || context.isHolding(AvengersBlocks.STAR_PORTAL_BLOCK.asItem())) ? VoxelShapes.fullCube() : VoxelShapes.empty();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) { return true; }

    public boolean canBucketPlace(BlockState state, Fluid fluid) { return false; }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, WATER_LEVEL, FLUID_FALL);
    }


    /** FluidFlowsThrough methods */

    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        int waterLevel = fluidState.getLevel();
        boolean water = fluidState.getFluid() == Fluids.WATER;

        return this.getDefaultState().with(WATERLOGGED, water).with(WATER_LEVEL, waterLevel);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        FluidState fluidState = world.getBlockState(fromPos).getFluidState();
        world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleFluidTick(pos, state.getFluidState().getFluid(), state.getFluidState().getFluid().getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        boolean falling = state.get(FLUID_FALL);

        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(falling);
        } else if (state.get(WATER_LEVEL) > 0) {
            return Fluids.WATER.getFlowing(state.get(WATER_LEVEL), falling);
        } else {
            return super.getFluidState(state);
        }
    }


    /** for BlockEntity */

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TeleportAnchorBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, TeleportAnchorBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        if (blockEntity instanceof TeleportAnchorBlockEntity teleportAnchorBlockEntity) {
            return teleportAnchorBlockEntity;
        } else {
            return null;
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TeleportAnchorBlockEntity teleportAnchorBlockEntity && teleportAnchorBlockEntity.age < 200L) {
            int i = teleportAnchorBlockEntity.getDrawnSidesCount();

            for(int j = 0; j < i; ++j) {
                double d = (double)pos.getX() + random.nextDouble();
                double e = (double)pos.getY() + random.nextDouble();
                double f = (double)pos.getZ() + random.nextDouble();
                double g = (random.nextDouble() - 0.5) * 0.5;
                double h = (random.nextDouble() - 0.5) * 0.5;
                double k = (random.nextDouble() - 0.5) * 0.5;
                int l = random.nextInt(2) * 2 - 1;
                if (random.nextBoolean()) {
                    f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
                    k = random.nextFloat() * 2.0F * (float)l;
                } else {
                    d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
                    g = random.nextFloat() * 2.0F * (float)l;
                }

                world.addParticle(GildingParticles.STAR_PARTICLE, d, e, f, g, h, k);
            }

        }
    }
}
