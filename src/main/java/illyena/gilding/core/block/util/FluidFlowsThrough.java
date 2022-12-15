package illyena.gilding.core.block.util;

import illyena.gilding.mixin.gilding.block.FlowableFluidAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/** <pre>
 * include in #init
 *     {@code this.setDefaultState(this.stateManager.getDefaultState()
 *              .with(WATERLOGGED, false)
 *              .with(WATER_LEVEL, 0)
 *              .with(FLUID_FALL, false));}
 *
 *
 * add properties to #appendProperties
 *      {@code protected void appendProperties(StateManager.Builder<Block, BlockState> builder){
 *              builder.add(WATERLOGGED, WATER_LEVEL, FLUID_FALL);
 *      }}
 *
 *
 * Override following methods in Block.class as shown
 *      {@code public BlockState getPlacementState(ItemPlacementContext context) {
 *              FluidState fluidState =
 *                  context.getWorld().getFluidState(context.getBlockPos());
 *              int waterLevel = fluidState.getLevel();
 *              boolean water = fluidState.getFluid() == Fluids.WATER;
 *
 *              return this.getDefaultState()
 *                  .with(WATERLOGGED, water).with(WATER_LEVEL, waterLevel);
 *      }}
 *
 *      {@code public void neighborUpdate(BlockState state, World world, BlockPos pos,
 *                  Block block, BlockPos fromPos, boolean notify) {
 *              super.neighborUpdate(state, world, pos, block, fromPos, notify);
 *
 *              FluidState fluidState =
 *                  world.getBlockState(fromPos).getFluidState();
 *              world.createAndScheduleFluidTick(pos, fluidState.getFluid(),
 *                  fluidState.getFluid().getTickRate(world));
 *      }}
 *
 *      {@code public BlockState getStateForNeighborUpdate(BlockState state,
 *                      Direction direction, BlockState neighborState,
 *                      WorldAccess world, BlockPos pos, BlockPos neighborPos) {
 *              world.createAndScheduleFluidTick(pos,
 *                  state.getFluidState().getFluid(),
 *                  state.getFluidState().getFluid().getTickRate(world));
 *
 *              return super.getStateForNeighborUpdate(state, direction,
 *                  neighborState, world, pos, neighborPos);
 *      }}
 *
 *      {@code public FluidState getFluidState(BlockState state) {
 *              boolean falling = state.get(FLUID_FALL);
 *
 *              if (state.get(WATERLOGGED)) {
 *                  return Fluids.WATER.getStill(falling);
 *               } else if (state.get(WATER_LEVEL) > 0) {
 *                  return Fluids.WATER.getFlowing(state.get(WATER_LEVEL), falling);
 *              } else {
 *                  return super.getFluidState(state);
 *              }
 *      } }
 *</pre>
 */
public interface FluidFlowsThrough extends Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final IntProperty WATER_LEVEL = Properties.LEVEL_8;
    public static final BooleanProperty FLUID_FALL = Properties.FALLING;

     /**
     * following methods adjusted from {@link Waterloggable} to allow {@link WaterFluid.Flowing}
     */
    public default boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return !(Boolean)state.get(Properties.WATERLOGGED) && fluid instanceof WaterFluid;
    }

    public default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() instanceof WaterFluid fluid ) {
            boolean falling = ((FlowableFluidAccessor)fluid).callMethod_15736(world, fluid, pos, state, pos.down(), world.getBlockState(pos.down()));

            world.setBlockState(pos, state.with(WATERLOGGED, fluidState.isStill()).with(WATER_LEVEL, fluidState.getLevel()).with(FLUID_FALL, falling), 3);
            world.createAndScheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));

            return true;

        } else return false;

    }

    public default ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.get(Properties.WATERLOGGED) || state.get(WATER_LEVEL) > 0) {
            world.setBlockState(pos, state.with(WATERLOGGED, false).with(WATER_LEVEL, 0).with(FLUID_FALL, false), 3);

            return state.get(WATERLOGGED) ? new ItemStack(Items.WATER_BUCKET) : ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

    }


}
