package illyena.gilding.avengers.block;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.core.particle.GildingParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class StarPortalBlock  extends BlockWithEntity {
    private static final Map<DyeColor, StarPortalBlock> STAR_PORTAL_BLOCKS = Maps.newIdentityHashMap();
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    @Nullable
    public DyeColor color;

    private static VoxelShape getHeadShape(BlockState state, BlockView world, BlockPos pos) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         return blockEntity instanceof StarPortalBlockEntity ? VoxelShapes.cuboid(((StarPortalBlockEntity)blockEntity).getHeadBoundingBox(state)) : VoxelShapes.empty();
     }

    private static VoxelShape getOpenShape(BlockState state, BlockView world, BlockPos pos) {
        VoxelShape shape = VoxelShapes.empty();
        switch (state.get(FACING)) {
            case UP -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.25, 1.0));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 1.0, 0.0, 1.0, 1.5, 1.0));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
            case DOWN -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.75, 0.0, 1.0, 1.0, 1.0));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, -0.5, 0.0, 1.0, 0.0, 1.0));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
            case NORTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, 0.75, 1.0, 1.0, 1.0));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, -0.5, 1.0, 1.0, 0.0));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
            case SOUTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0, 1.0, 1.0, 1.0, 1.5));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
            case WEST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.0, 0.0, 1.0, 1.0, 1.0));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.5, 0.0, 0.0, 0.0, 1.0, 1.0));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
            case EAST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.25, 1.0, 1.0));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(1.0, 0.0, 0.0, 1.5, 1.0, 1.0));
                shape = VoxelShapes.union(shape, getHeadShape(state, world, pos));
            }
        }
        return shape;
    }

    protected StarPortalBlock(@Nullable DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
        STAR_PORTAL_BLOCKS.put(color, this);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new StarPortalBlockEntity(this.color, pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity>BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else {
            if (world.getBlockEntity(pos) instanceof StarPortalBlockEntity blockEntity) {
                ItemStack stack = player.getStackInHand(hand);
                Box headBox = blockEntity.getHeadBoundingBox(state).expand(0.01).offset(pos);

                if ((blockEntity.getAnimationStage() != StarPortalBlockEntity.AnimationStage.OPENED || !headBox.contains(hitResult.getPos()))
                        && stack.getItem() instanceof DyeItem dyeItem && dyeItem.getColor() != this.color) {
                    blockEntity.getWorld().playSound(player, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    if (!world.isClient()) {
                        BlockState newState = StarPortalBlock.get(dyeItem.getColor()).getDefaultState().with(FACING, state.get(FACING));
                        world.setBlockState(pos, newState);
                        stack.decrement(1);
                    }
                    return ActionResult.success(player.world.isClient);
                }
                else if (blockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED
                         && blockEntity.getHeadBoundingBox(state).expand(0.01).offset(pos).contains(hitResult.getPos())) {
                    if (!world.isClient()) {
                        if (stack.isEmpty()) {
                            StarPortalBlockEntity.tryTeleportingEntity(world, pos, state, player, blockEntity);
                        } else {
                            ItemStack stack2 = stack.split(1);
                            ItemEntity itemEntity = player.dropStack(stack2);
                            StarPortalBlockEntity.tryTeleportingEntity(world, pos, state, itemEntity, blockEntity);
                        }
                    }
                    return ActionResult.success(player.world.isClient);
                }
            }
            return ActionResult.PASS;
        }

    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.hasVehicle()
                && !entity.hasPassengers()
                && entity.canUsePortals()
                && world.getBlockEntity(pos) instanceof StarPortalBlockEntity blockEntity
                && blockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED
                && blockEntity.getHeadBoundingBox(state).expand(0.01).offset(pos).intersects(entity.getBoundingBox())) {
            StarPortalBlockEntity.tryTeleportingEntity(world, pos, state, entity, blockEntity);
        }
    }

    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos pos = hit.getBlockPos();
        if (world.getBlockEntity(pos) instanceof StarPortalBlockEntity blockEntity
               && blockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED
               && blockEntity.getHeadBoundingBox(state).expand(0.01).offset(pos).intersects(projectile.getBoundingBox())) {
           StarPortalBlockEntity.tryTeleportingEntity(world, pos, state, projectile, blockEntity);
        }

    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        this.teleport(state, world, pos);
    }

    private void teleport(BlockState state, World world, BlockPos pos) {
        WorldBorder worldBorder = world.getWorldBorder();
        for(int i = 0; i < 1000; ++i) {
            BlockPos blockPos = pos.add(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));
            Direction direction = this.getAttachDirection(world, pos, blockPos);
            if (world.getBlockState(blockPos).isAir() && worldBorder.contains(blockPos) && direction != null) {
                if (world.isClient) {
                    for(int j = 0; j < 128; ++j) {
                        double d = world.random.nextDouble();
                        float f = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float g = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float h = (world.random.nextFloat() - 0.5F) * 0.2F;
                        double e = MathHelper.lerp(d, blockPos.getX(), pos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
                        double k = MathHelper.lerp(d, blockPos.getY(), pos.getY()) + world.random.nextDouble() - 0.5;
                        double l = MathHelper.lerp(d, blockPos.getZ(), pos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
                        world.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                    }
                } else {
                    world.setBlockState(blockPos, state.with(FACING, direction.getOpposite()), Block.NOTIFY_LISTENERS);
                    world.removeBlock(pos, false);
                }
                return;
            }
        }

    }

    private Direction getAttachDirection(World world , BlockPos startPos, BlockPos pos) {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos blockPos2 = pos.offset(direction);
            BlockState blockState2 = world.getBlockState(blockPos2);
            Block block2 = blockState2.getBlock();
            VoxelShape shape2 = block2.getSidesShape(blockState2, world, blockPos2);
            boolean bl = (!(blockState2.isAir()) && Block.isFaceFullSquare(shape2.getFace(direction.getOpposite()), direction.getOpposite()) && blockPos2 != startPos);
            if (bl) {
                return direction;
            }
        }
       return null;
    }

    public static boolean canOpen(BlockState state, World world, BlockPos pos, StarPortalBlockEntity blockEntity) {
        if (blockEntity.getAnimationStage() != StarPortalBlockEntity.AnimationStage.CLOSED) {
            return true;
        } else {
            Box box = ShulkerEntity.calculateBoundingBox(state.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6);
            return world.isSpaceEmpty(box);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity){
            return starPortalBlockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED ? getOpenShape(state, world, pos) : VoxelShapes.cuboid(starPortalBlockEntity.getBoundingBox(state));
        } else return VoxelShapes.fullCube();
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            if (world.getBlockEntity(pos) instanceof StarPortalBlockEntity blockEntity) {
                ItemStack itemStack = new ItemStack(this);
                BlockPos exitPortalPos = blockEntity.getExitPortalPos();
                NbtCompound nbt;
                if (exitPortalPos != null) {
                    nbt = new NbtCompound();
                    nbt.put("ExitPortal", NbtHelper.fromBlockPos(exitPortalPos));
                    BlockItem.setBlockEntityNbt(itemStack, AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, nbt);
                }
                if (blockEntity.getExactTeleport()) {
                    nbt = new NbtCompound();
                    nbt.putBoolean("ExactTeleport", true);
                }
                nbt = new NbtCompound();
                itemStack.setSubNbt("BlockStateTag", nbt);
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
        super.onBreak(world, pos, state, player);
   }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StarPortalBlockEntity) {
            int i = ((StarPortalBlockEntity)blockEntity).getDrawnSidesCount();

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

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) { return ItemStack.EMPTY; }

    public boolean canBucketPlace(BlockState state, Fluid fluid) { return false; }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return AvengersBlocks.STAR_PORTAL_BLOCK;
        } else {
            return switch (dyeColor) {
                case WHITE -> AvengersBlocks.WHITE_STAR_PORTAL_BLOCK;
                case ORANGE -> AvengersBlocks.ORANGE_STAR_PORTAL_BLOCK;
                case MAGENTA -> AvengersBlocks.MAGENTA_STAR_PORTAL_BLOCK;
                case LIGHT_BLUE -> AvengersBlocks.LIGHT_BLUE_STAR_PORTAL_BLOCK;
                case YELLOW -> AvengersBlocks.YELLOW_STAR_PORTAL_BLOCK;
                case LIME -> AvengersBlocks.LIME_STAR_PORTAL_BLOCK;
                case PINK -> AvengersBlocks.PINK_STAR_PORTAL_BLOCK;
                case GRAY -> AvengersBlocks.GRAY_STAR_PORTAL_BLOCK;
                case LIGHT_GRAY -> AvengersBlocks.LIGHT_GRAY_STAR_PORTAL_BLOCK;
                case CYAN -> AvengersBlocks.CYAN_STAR_PORTAL_BLOCK;
                case PURPLE -> AvengersBlocks.PURPLE_STAR_PORTAL_BLOCK;
                case BLUE -> AvengersBlocks.BLUE_STAR_PORTAL_BLOCK;
                case BROWN -> AvengersBlocks.BROWN_STAR_PORTAL_BLOCK;
                case GREEN -> AvengersBlocks.GREEN_STAR_PORTAL_BLOCK;
                case RED -> AvengersBlocks.RED_STAR_PORTAL_BLOCK;
                case BLACK -> AvengersBlocks.BLACK_STAR_PORTAL_BLOCK;
            };
        }
    }

    @Nullable
    public  DyeColor getColor() { return  this.color; }

    public static Iterable<StarPortalBlock> getAll() {
        return Iterables.unmodifiableIterable(STAR_PORTAL_BLOCKS.values());
    }

}



