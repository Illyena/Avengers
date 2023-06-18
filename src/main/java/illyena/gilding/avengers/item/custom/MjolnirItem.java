package illyena.gilding.avengers.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.entity.projectile.MjolnirEntity;
import illyena.gilding.avengers.util.data.AvengersBlockTagGenerator;
import illyena.gilding.core.item.IThrowable;
import illyena.gilding.core.item.util.GildingToolMaterials;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static illyena.gilding.avengers.AvengersInit.translationKeyOf;

public class MjolnirItem extends BlockItem implements IThrowable {
    private final ToolMaterial material = GildingToolMaterials.MAGIC;
    private final TagKey<Block> effectiveBlocks = GildingBlockTagGenerator.MAGIC_MINEABLE;
    public final float miningSpeed = this.material.getMiningSpeedMultiplier();
    private final float attackDamage = 8.0f;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public MjolnirItem(Settings settings) {
        super(AvengersBlocks.MJOLNIR_BLOCK, settings.maxDamageIfAbsent(1561));
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) { return !miner.isCreative(); }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.getDamage() >= stack.getMaxDamage() - 1) {
            return TypedActionResult.fail(stack);
        } else {
            user.setCurrentHand(hand);
            if (!this.isWorthy(user)) {
                return TypedActionResult.fail(stack);
            }
            return super.use(world, user, hand);
        }
    }

    public boolean isDamageable() { return false; }

    public UseAction getUseAction(ItemStack stack) { return UseAction.SPEAR; }

    public int getMaxUseTime(ItemStack stack) { return 72000; }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        boolean inMainHand = (user.getMainHandStack() == stack);
        if (this.isWorthy(user) || user instanceof PlayerEntity player && player.isCreative()) {
            if (this.getMaxUseTime(stack) - remainingTicks >= 10) {
                if (inMainHand) {
                    this.onThrow(stack, world, user, remainingTicks);
                } else this.onRiptide(stack, world, user, remainingTicks);
            }
        } else {
            if (user instanceof PlayerEntity player) {
                player.sendMessage(translationKeyOf("message", "not_worthy"));
            }
        }
    }

    private void onRiptide(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        if (this.getMaxUseTime(stack) - remainingTicks >= 10) {
            if (user instanceof PlayerEntity player) {
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
           if (true) { //EnchantmentHelper.getRiptide(stack) > 0) {
                float f = user.getYaw();
                float g = user.getPitch();
                float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                float k = -MathHelper.sin(g * 0.017453292F);
                float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                float m = MathHelper.sqrt(h * h + k * k + l * l);
                float n = 3.0F;
                h *= n / m;
                k *= n / m;
                l *= n / m;
                user.addVelocity(h, k, l);
                if (user instanceof PlayerEntity player) {
                    player.useRiptide(20);
                }
                if (user.isOnGround()) {
                    user.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
                }

                SoundEvent soundEvent;
                soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                world.playSoundFromEntity(null, user, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    private boolean isWorthy (LivingEntity user) {
        return user instanceof PlayerEntity player && (player.experienceLevel >= 30 || player.isCreative());
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }

    //MiningToolItem
    public ToolMaterial getMaterial() { return this.material; }

    public int getEnchantability() { return this.material.getEnchantability(); }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(this.effectiveBlocks) ? this.miningSpeed : 1.0f;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(0, attacker, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.damage(0, miner, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public float getAttackDamage() { return this.attackDamage; }

    public boolean isSuitableFor(BlockState state) { return state.isIn(AvengersBlockTagGenerator.NEEDS_TOOL_LEVEL_5); }

    //Block Item
    public Block getBlock() { return AvengersBlocks.MJOLNIR_BLOCK; }

    //IThrowable
    @Override
    public PersistentProjectileEntity getProjectileEntity(World world, PlayerEntity playerEntity, ItemStack stack) {
        return new MjolnirEntity(world, playerEntity, stack);
    }

    public float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public boolean canThrow(ItemStack stack, World world, LivingEntity user, int remainingTicks) { return isWorthy(user); }

}