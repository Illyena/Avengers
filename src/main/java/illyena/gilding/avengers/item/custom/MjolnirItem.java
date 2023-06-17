package illyena.gilding.avengers.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.entity.projectile.MjolnirEntity;
import illyena.gilding.avengers.util.data.AvengersBlockTagGenerator;
import illyena.gilding.core.item.IThrowable;
import illyena.gilding.core.item.Unbreakable;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.terraformersmc.modmenu.util.TranslationUtil.translationKeyOf;

public class MjolnirItem extends AliasedBlockItem implements IThrowable, Unbreakable {
    private final ToolMaterial material;
    private final TagKey<Block> effectiveBlocks;
    public final float miningSpeed;
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public MjolnirItem(Block block, float attackDamage, float attackSpeed, ToolMaterials material, Settings settings) {
        super(block, settings.maxDamageIfAbsent(material.getDurability()));
        this.material = material;
        this.effectiveBlocks = GildingBlockTagGenerator.MAGIC_MINEABLE;
        this.miningSpeed = material.getMiningSpeedMultiplier();
        this.attackDamage = attackDamage + material.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier",
                isUsable(this.getDefaultStack()) ? this.attackDamage : 0.0f, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier",
                isUsable(this.getDefaultStack()) ? attackSpeed : -3.2f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public ToolMaterial getMaterial() { return this.material; }

    public int getEnchantability() { return this.material.getEnchantability(); }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(this.effectiveBlocks) && this.isUsable(stack) ? this.miningSpeed : 0.01f;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isUsable(stack)) {
            stack.damage(1, attacker, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return this.isUsable(stack);
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (this.isUsable(stack) &&!world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return this.isUsable(stack);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(EquipmentSlot.OFFHAND);
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public boolean isSuitableFor(BlockState state) { return state.isIn(AvengersBlockTagGenerator.NEEDS_TOOL_LEVEL_5); }

    public ActionResult useOnBlock(ItemUsageContext context) {
        return Objects.requireNonNull(context.getPlayer()).isSneaking() ? this.place(new ItemPlacementContext(context)) : ActionResult.PASS;
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return state.isIn(AvengersBlockTagGenerator.NEEDS_TOOL_LEVEL_5);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!isUsable(stack)) {
            return TypedActionResult.fail(stack);
        } else {
            user.setCurrentHand(hand);
            if (!isWorthy(user)) {
                this.notWorthy(stack, world, user);
                return TypedActionResult.fail(stack);
            }
            return TypedActionResult.consume(stack);
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        boolean bl = user.getMainHandStack() == stack;
        if (isWorthy(user) || (user instanceof PlayerEntity player && player.isCreative())) {
            if (this.getMaxUseTime(stack) - remainingTicks >= 10) {
                if(bl) {
                    this.onThrow(stack, world, user, remainingTicks);
                } else this.onRiptide(stack, world, user, remainingTicks);
            }
        } else {
            this.notWorthy(stack, world, user);
            if (user instanceof PlayerEntity player) {
                player.sendMessage(Text.translatable(translationKeyOf("message", "not_worthy")));
            }
        }
    }

    private void onRiptide(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
            int i = this.getMaxUseTime(stack) - remainingTicks;
            if (i >= 10) {
                int j = EnchantmentHelper.getRiptide(stack);
                if (user instanceof PlayerEntity playerEntity) {
                    if (!world.isClient) {
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(user.getActiveHand()));
                    }
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
                if (j > 0) {
                    float f = user.getYaw();
                    float g = user.getPitch();
                    float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                    float k = -MathHelper.sin(g * 0.017453292F);
                    float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                    float m = MathHelper.sqrt(h * h + k * k + l * l);
                    float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                    h *= n / m;
                    k *= n / m;
                    l *= n / m;
                    user.addVelocity(h, k, l);
                    if (user instanceof PlayerEntity playerEntity) {
                        playerEntity.useRiptide(20);
                    }

                    if (user.isOnGround()) {
                        float o = 1.1999999F;
                        user.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
                    }

                    SoundEvent soundEvent;
                    if (j >= 3) {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                    } else if (j == 2) {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                    } else {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                    }

                    world.playSoundFromEntity(null, user, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
    }

    private boolean isWorthy(LivingEntity user) {
        if ( user instanceof PlayerEntity player && (player.experienceLevel >= 30 || player.isCreative())) {
            return true;
        } else return user instanceof WolfEntity wolf && wolf.isTamed();
    }

    private void notWorthy(ItemStack stack, World world, LivingEntity user) {
        boolean bl = user.getMainHandStack() == stack;
        BlockPos pos = user.getBlockPos();
        Direction direction = bl ? user.getHorizontalFacing() : user.getHorizontalFacing().getOpposite();
        BlockPos pos2;
        switch (direction) {
            case EAST -> pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
            case WEST -> pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
            case NORTH -> pos2 = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
            case SOUTH -> pos2 = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            default -> pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        }
        toBlock(stack, world, user, pos2, user.getHorizontalFacing());
        removeStack(stack, world, user);
    }

    public void toBlock(ItemStack stack, World world, LivingEntity entity, BlockPos pos, Direction facing) {
        Block block = AvengersBlocks.MJOLNIR_BLOCK;
        BlockState state = block.getDefaultState().with(MjolnirBlock.FACING, facing);
        world.setBlockState(pos, state);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(entity, state));
        if (world.getBlockEntity(pos) instanceof MjolnirBlockEntity blockEntity) {
            blockEntity.setDamage(stack.getDamage());
        }
    }

    private void removeStack(ItemStack stack, World world, LivingEntity user) {
        if ( user instanceof PlayerEntity player) {
            player.getInventory().removeOne(stack);
        }
        if (user instanceof InventoryOwner owner) {
            owner.getInventory().removeItem(stack.getItem(), 1);
        }
    }


    public UseAction getUseAction(ItemStack stack) { return isUsable(stack) ? UseAction.SPEAR : UseAction.NONE; }

    public int getMaxUseTime(ItemStack stack) { return 72000; }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }

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

}