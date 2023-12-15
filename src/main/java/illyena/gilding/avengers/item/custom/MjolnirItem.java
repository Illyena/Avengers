package illyena.gilding.avengers.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import illyena.gilding.avengers.advancement.AvengersAdvancements;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.entity.projectile.MjolnirEntity;
import illyena.gilding.avengers.util.data.AvengersBlockTagGenerator;
import illyena.gilding.core.item.IThrowable;
import illyena.gilding.core.item.IThunderous;
import illyena.gilding.core.item.IUndestroyable;
import illyena.gilding.core.item.util.BlockEntityItem;
import illyena.gilding.core.item.util.GildingToolMaterials;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import illyena.gilding.mixin.item.BlockItemAccessor;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Iterator;

import static illyena.gilding.avengers.block.MjolnirBlock.FACING;
import static net.minecraft.block.Block.dropStack;

public class MjolnirItem extends BlockItem implements BlockEntityItem, IThrowable, IThunderous, IUndestroyable {
    private static final ToolMaterial material = GildingToolMaterials.MAGIC;
    private final TagKey<Block> effectiveBlocks;
    public final float miningSpeed;
    private final float attackDamage = 8.0f + material.getAttackDamage();
    private final float attackSpeed = -2.9f;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public MjolnirItem(Block block, Settings settings) {
        super(block, settings.maxDamageIfAbsent(material.getDurability()));
        this.effectiveBlocks = GildingBlockTagGenerator.MAGIC_MINEABLE;
        this.miningSpeed = material.getMiningSpeedMultiplier();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier",
                isUsable(this.getDefaultStack()) ? this.attackDamage : 0.0f, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier",
                isUsable(this.getDefaultStack()) ? this.attackSpeed : -3.2f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return state.isIn(AvengersBlockTagGenerator.NEEDS_TOOL_LEVEL_5);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(this.effectiveBlocks) && this.isUsable(stack) ? this.miningSpeed + 6.0f : 0.01f;
    }

    public boolean isSuitableFor(BlockState state) { return state.isIn(AvengersBlockTagGenerator.NEEDS_TOOL_LEVEL_5); }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isUsable(stack)) {
            stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            return true;
        }
        return false;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        int amount = Math.min(2, stack.getMaxDamage() - stack.getDamage() - 1);
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            stack.damage(amount, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public int getEnchantability() { return this.material.getEnchantability(); }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    public UseAction getUseAction(ItemStack stack) { return isUsable(stack) ? UseAction.SPEAR : UseAction.NONE; }

    public int getMaxUseTime(ItemStack stack) { return 72000; }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!isUsable(itemStack)) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            if (!isWorthy(user)) {
                this.notWorthy(itemStack, world, user);
                return TypedActionResult.fail(itemStack);
            } else if (hand.equals(Hand.OFF_HAND) && EnchantmentHelper.getRiptide(itemStack) > 0 && !(world.isRaining() || world.isThundering())) {
                return  TypedActionResult.fail(itemStack);
            }
            return TypedActionResult.consume(itemStack);
        }
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity player && player.isSneaking()) {
            this.callThunder(stack, world, user, remainingUseTicks);
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        if (this.isWorthy(user) || (user instanceof PlayerEntity player && player.isCreative())) {
            if (this.getMaxUseTime(stack) - remainingTicks >= 10) {
                this.onThrow(stack, world, user, remainingTicks);
            }
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemPlacementContext ctx = new ItemPlacementContext(context);

        ItemStack itemStack = ctx.getStack().copy();
        World world = ctx.getWorld();
        PlayerEntity player = ctx.getPlayer();
        BlockPos blockPos = ctx.getBlockPos();
        if (player != null && player.isSneaking() && player.getActiveHand() != Hand.OFF_HAND) {
            if (toBlock(itemStack, world, player, blockPos, 0)) {
                if (!player.getAbilities().creativeMode) {
                    ctx.getStack().decrement(1);
                }
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.PLACED_BLOCK.trigger(serverPlayer, blockPos, itemStack);
                }
                return ActionResult.success(world.isClient());
            }
        }
        return ActionResult.PASS;
    }

    public boolean isWorthy(LivingEntity user) {
        if ( user instanceof PlayerEntity player && (player.experienceLevel >= 30 || player.isCreative())) {
            return true;
        } else return user instanceof WolfEntity wolf && wolf.isTamed();
    }

    private void notWorthy(ItemStack stack, World world, LivingEntity user) {
        boolean bl = user.getMainHandStack() == stack;
        BlockPos blockPos = user.getBlockPos();
        Direction direction = bl ? user.getHorizontalFacing() : user.getHorizontalFacing().getOpposite();
        BlockPos pos2;
        switch (direction) {
            case EAST -> pos2 = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
            case WEST -> pos2 = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1);
            case NORTH -> pos2 = new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
            case SOUTH -> pos2 = new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
            default -> pos2 = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        boolean bl2 = this.toBlock(stack, world, user, pos2, 1);
        if (!bl2) {
            dropStack(world, pos2, stack);
        }
        if (user instanceof PlayerEntity playerEntity && !playerEntity.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            AvengersAdvancements.NOT_WORTHY.trigger(serverPlayerEntity, stack, blockPos);
        }

    }

    /** BlockEntityItem */
    public boolean toBlock(ItemStack stack, World world, Entity entity, BlockPos blockPos, int radius) {
        BlockState blockState = this.getBlock().getDefaultState().with(FACING, entity.getHorizontalFacing()).with(Properties.WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER);
        if (this.getBlock().isEnabled(world.getEnabledFeatures())) {
            Box box = new Box(blockPos, blockPos);
            int i = 0;
            BlockPos blockPos2 = blockPos;
            boolean bl = false;

            do {
                box = box.expand(i);
                BlockPos boxPos1 = new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
                BlockPos boxPos2 = new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);
                Iterator<BlockPos> iterator = BlockPos.iterate(boxPos1, boxPos2).iterator();
                do {
                    BlockPos blockPos3 = iterator.next();
                    if (canPlaceAt(world, entity, blockState, blockPos3)) {
                        blockPos2 = blockPos3;
                        bl = true;
                        break;
                    }

                } while (iterator.hasNext());
                ++i;
            } while (i <= radius && !canPlaceAt(world, entity, blockState, blockPos2));

            if (bl && world.setBlockState(blockPos2, blockState, 11, 512)) {
                PlayerEntity player = entity instanceof PlayerEntity playerEntity ? playerEntity : null;
                BlockState blockState2 = world.getBlockState(blockPos);
                if (blockState2.isOf(blockState.getBlock())) {
                    blockState2 = ((BlockItemAccessor) this).callPlaceFromNbt(blockPos, world, stack, blockState2);
                    this.postPlacement(blockPos, world, player, stack, blockState2);
                    blockState2.getBlock().onPlaced(world, blockPos, blockState2, entity instanceof LivingEntity livingEntity ? livingEntity : null, stack);
                }
                if (world.getBlockEntity(blockPos2) instanceof MjolnirBlockEntity mjolnirBlockEntity) {
                    mjolnirBlockEntity.setDamage(stack.getDamage(), stack);
                    mjolnirBlockEntity.setEnchantments(stack.getEnchantments());
                    mjolnirBlockEntity.setCustomName(stack.hasCustomName() ? stack.getName() : null);
                }

                BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                world.playSound(entity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(entity, blockState2));
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceAt(World world, Entity entity, BlockState blockState, BlockPos blockPos) {
        return blockState.canPlaceAt(world, blockPos) && world.getBlockState(blockPos).isReplaceable() &&
                world.canPlace(blockState, blockPos, ShapeContext.of(entity)) &&
                world.getBlockState(blockPos).getBlock() != blockState.getBlock();
    }


    /** IThrowable */
    @Override
    public PersistentProjectileEntity getProjectileEntity(World world, PlayerEntity playerEntity, ItemStack stack) {
        return new MjolnirEntity(world, playerEntity, stack);
    }

    @Override
    public float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public boolean canThrow(ItemStack stack, World world, LivingEntity user, int remainingTicks) { return isWorthy(user); }

    @Override
    public int getRiptide(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        return user.getActiveHand().equals(Hand.OFF_HAND) && world.isRaining() ? EnchantmentHelper.getRiptide(stack) : 0 ;
    }

}