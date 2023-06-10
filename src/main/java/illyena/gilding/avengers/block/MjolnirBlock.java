package illyena.gilding.avengers.block;

import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MjolnirBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

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
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH ? composeNSShape() : composeEWShape();
    }

    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else if (player.experienceLevel >= 30 || player.isCreative()) {
            if (!player.isCreative()) {
                ItemStack stack = new ItemStack(this.asItem());
                if (world.getBlockEntity(pos) instanceof MjolnirBlockEntity blockEntity) {
                    stack.setDamage(blockEntity.getDamage());
                }
                player.getInventory().insertStack(stack);
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return ActionResult.SUCCESS;
        } else {
            player.addExhaustion(player.isSneaking() ? 1.5f : 1.0f);
            return ActionResult.PASS;
        }
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing());
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MjolnirBlockEntity mjolnir && itemStack.hasNbt()) {
            NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound.contains("Damage")) {
                mjolnir.setDamage(nbtCompound.getInt("Damage"));
            }
        } if (itemStack.hasCustomName()) {
            world.getBlockEntity(pos, BlockEntityType.BANNER).ifPresent((blockEntity1) -> {
                blockEntity1.setCustomName(itemStack.getName());
            });
        }

    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MjolnirBlockEntity(pos, state);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }
}
//todo gravity