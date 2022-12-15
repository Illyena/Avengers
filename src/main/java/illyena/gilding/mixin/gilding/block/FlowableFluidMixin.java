package illyena.gilding.mixin.gilding.block;

import illyena.gilding.core.block.util.FluidFlowsThrough;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Allows {@link WaterFluid} to flow through {@link Block}s */
@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {
    @Shadow @Final public static BooleanProperty FALLING;
    private World world;
    private BlockPos pos;
    private FluidState fluidState;


    /** dummy inject to capture locals */
    @Inject(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FlowableFluid;getNextTickDelay(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;Lnet/minecraft/fluid/FluidState;)I", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onOnScheduledTick(World world, BlockPos pos, FluidState state, CallbackInfo ci, FluidState fluidState) {
        this.world = world;
        this.pos = pos;
        this.fluidState = fluidState;
    }

    @ModifyArg(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0), index = 1)
    private BlockState replaceEmptyBlockState(BlockState state) {
        return isFluidFlowsThrough(world, pos) ? setFFTBlockState(world, pos, false, 0, false) : Blocks.AIR.getDefaultState();

    }

    @ModifyArg(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1), index = 1)
    private BlockState replaceUnequalBlockState(BlockState state) {
        return isFluidFlowsThrough(world, pos) ? setFFTBlockState(world, pos, fluidState) : state;

    }
    private static boolean isFluidFlowsThrough(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof FluidFlowsThrough;
    }

    private static BlockState setFFTBlockState(World world, BlockPos pos, boolean isStill, int level, boolean falling) {
        return world.getBlockState(pos).with(FluidFlowsThrough.WATERLOGGED, isStill).with(FluidFlowsThrough.WATER_LEVEL, level).with(FluidFlowsThrough.FLUID_FALL, false);
    }

    private static BlockState setFFTBlockState(World world, BlockPos pos, FluidState fluidState) {
        return world.getBlockState(pos).with(FluidFlowsThrough.WATERLOGGED, fluidState.isStill()).with(FluidFlowsThrough.WATER_LEVEL, fluidState.getLevel()).with(FluidFlowsThrough.FLUID_FALL, fluidState.get(FALLING));
    }

}
