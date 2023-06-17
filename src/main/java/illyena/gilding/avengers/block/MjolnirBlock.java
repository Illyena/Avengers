package illyena.gilding.avengers.block;

import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.tag.BlockTags;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
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
            world.getBlockEntity(pos, BlockEntityType.BANNER).ifPresent((blockEntity1) ->
                    blockEntity1.setCustomName(itemStack.getName()));
        }

    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.createAndScheduleBlockTick(pos, this, this.getFallDelay());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.createAndScheduleBlockTick(pos, this, this.getFallDelay());
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            this.configureFallingBlockEntity(fallingBlockEntity);
        }
    }

    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(2.0f, 40);
    }

    protected int getFallDelay() {
        return 2;
    }

    public static boolean canFallThrough(BlockState state) {
        Material material = state.getMaterial();
        return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockPos = pos.down();
            if (canFallThrough(world.getBlockState(blockPos))) {
                double d = (double)pos.getX() + random.nextDouble();
                double e = (double)pos.getY() - 0.05;
                double f = (double)pos.getZ() + random.nextDouble();
                world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
            }
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
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}