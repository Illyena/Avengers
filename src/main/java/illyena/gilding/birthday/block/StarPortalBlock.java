package illyena.gilding.birthday.block;

import illyena.gilding.GildingInit;
import illyena.gilding.birthday.block.blockentity.BirthdayBlockEntities;
import illyena.gilding.birthday.block.blockentity.StarPortalBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StarPortalBlock  extends BlockWithEntity {
   public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
   @Nullable
   private final DyeColor color;

   private static VoxelShape getHeadShape(BlockState state, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);;
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

    protected StarPortalBlock(Settings settings) {
        super(settings);
        this.color = null;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new StarPortalBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity>BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BirthdayBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntity::tick);
//        return checkType(type, BirthdayBlockEntities.STAR_PORTAL_BLOCK_ENTITY, world.isClient ? StarPortalBlockEntity::clientTick : StarPortalBlockEntity::serverTick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }


    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
       if (player.isSpectator()) {
           return ActionResult.CONSUME;
       } else {
          if (world.getBlockEntity(pos) instanceof StarPortalBlockEntity blockEntity) {
            GildingInit.LOGGER.warn("hit" + hitResult.getPos());
            GildingInit.LOGGER.warn("HEAD" + " x: " + blockEntity.getHeadBoundingBox(state).offset(pos).getMin(Direction.Axis.X) + " - " + blockEntity.getHeadBoundingBox(state).offset(pos).getMax(Direction.Axis.X)
                    + " y: " + blockEntity.getHeadBoundingBox(state).offset(pos).getMin(Direction.Axis.Y) + " - " + blockEntity.getHeadBoundingBox(state).offset(pos).getMax(Direction.Axis.Y)
                    + " z: " + blockEntity.getHeadBoundingBox(state).offset(pos).getMin(Direction.Axis.Z) + " - " + blockEntity.getHeadBoundingBox(state).offset(pos).getMax(Direction.Axis.Z));


            if (blockEntity.animationStage == StarPortalBlockEntity.AnimationStage.CLOSED || blockEntity.animationStage == StarPortalBlockEntity.AnimationStage.CLOSING) {
                   blockEntity.animationStage = StarPortalBlockEntity.AnimationStage.OPENING;
                   return ActionResult.CONSUME;
               } else if (blockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED && blockEntity.getHeadBoundingBox(state).expand(0.01).offset(pos).contains(hitResult.getPos())) {
                   GildingInit.LOGGER.error("PORTAL" );
                  if (!world.isClient) {
//                       EntityType.COW.spawn(((ServerWorld) world), null, null, null, pos, SpawnReason.COMMAND, true, false);
                       StarPortalBlockEntity.tryTeleportingEntity((ServerWorld)world, pos, state, player, blockEntity);
                   }
                   return ActionResult.CONSUME;
               } else {
                   blockEntity.animationStage = StarPortalBlockEntity.AnimationStage.CLOSING;
                   return ActionResult.CONSUME;
               }

           } else return ActionResult.PASS;
       }
    }

    public static boolean canOpen(BlockState state, World world, BlockPos pos, StarPortalBlockEntity blockEntity) {
        if (blockEntity.getAnimationStage() != StarPortalBlockEntity.AnimationStage.CLOSED) {
            return true;
        } else {
            Box box = ShulkerEntity.calculateBoundingBox((Direction)state.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6);
            return world.isSpaceEmpty(box);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getSide());
    }

/*    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity) {
            if (!world.isClient && player.isCreative() && !starPortalBlockEntity.isEmpty()) {
                ItemStack itemStack = getItemStack(this.getColor());
                blockEntity.setStackNbt(itemStack);
                if (starPortalBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(starPortalBlockEntity.getCustomName());
                }

                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                starPortalBlockEntity.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }*/ //todo john suggests teleporting to exit location


    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.NORMAL; } //todo

/*    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof StarPortalBlockEntity ? VoxelShapes.cuboid(((StarPortalBlockEntity)blockEntity).getBoundingBox(state)) : VoxelShapes.fullCube();
    }
*/
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof StarPortalBlockEntity starPortalBlockEntity){
            return starPortalBlockEntity.getAnimationStage() == StarPortalBlockEntity.AnimationStage.OPENED ? getOpenShape(state, world, pos) : VoxelShapes.cuboid(starPortalBlockEntity.getBoundingBox(state));
        } else return VoxelShapes.fullCube();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
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
                    k = (double)(random.nextFloat() * 2.0F * (float)l);
                } else {
                    d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
                    g = (double)(random.nextFloat() * 2.0F * (float)l);
                }

                world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, k);
            }

        }
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    @Nullable
    public static DyeColor getColor(Item item) { return getColor(Block.getBlockFromItem(item)); }

    @Nullable
    public static DyeColor getColor(Block block) {
        return block instanceof StarPortalBlock ? ((StarPortalBlock)block).getColor() : null;
    }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return BirthdayBlocks.STAR_PORTAL_BLOCK;
        } else {
            return switch (dyeColor) {
                case WHITE -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case ORANGE -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case MAGENTA -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case LIGHT_BLUE -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case YELLOW -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case LIME -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case PINK -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case GRAY -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case LIGHT_GRAY -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case CYAN -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case PURPLE -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case BLUE -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case BROWN -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case GREEN -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case RED -> BirthdayBlocks.STAR_PORTAL_BLOCK;
                case BLACK -> BirthdayBlocks.STAR_PORTAL_BLOCK;
            };
        }
    } //todo colored star portals

    @Nullable
    public  DyeColor getColor() { return  this.color; }

    public static ItemStack getItemStack(@Nullable DyeColor color) {
        return new ItemStack(get(color));
    }
} //todo change particles to stars


//todo currently breaks with survival fist
