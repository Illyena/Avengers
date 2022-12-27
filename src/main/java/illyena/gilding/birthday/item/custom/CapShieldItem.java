package illyena.gilding.birthday.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import illyena.gilding.birthday.entity.projectile.CapShieldEntity;
import illyena.gilding.birthday.util.data.BirthdayBlockTagGenerator;
import illyena.gilding.core.item.IThrowable;
import illyena.gilding.core.item.util.GildingToolMaterials;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import illyena.gilding.core.util.data.GildingItemTagGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapShieldItem extends MiningToolItem implements IThrowable {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public CapShieldItem(FabricItemSettings settings) {
        super( 3, -2.0f, GildingToolMaterials.MAGIC, GildingBlockTagGenerator.MAGIC_MINEABLE, settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier",
                (double)(isUsable(this.getDefaultStack()) ? 3.0f : 0.0f), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier",
                (double)(isUsable(this.getDefaultStack()) ? -2.0f : -3.2f), EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f;
            });
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricModelPredicateProviderRegistry.register(new Identifier("broken_shield"), (stack, world, entity, seed) -> {
                return stack.getItem() instanceof CapShieldItem && isUsable(stack) ? 0.0F : 1.0F;
            });
        }
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(EquipmentSlot.OFFHAND);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return state.isIn(BirthdayBlockTagGenerator.NEEDS_TOOL_LEVEL_5);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(GildingBlockTagGenerator.MAGIC_MINEABLE) && isUsable(stack) ? this.miningSpeed + 6.0f : 0.01f;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return isUsable(stack) ? super.postHit(stack, target, attacker) : false;
    }

    public static boolean isUsable(ItemStack stack) { return stack.getDamage() < stack.getMaxDamage() - 1; }

    public UseAction getUseAction(ItemStack stack) { return isUsable(stack) ? UseAction.BLOCK : UseAction.NONE; }

    public int getMaxUseTime(ItemStack stack) { return 72000; }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!isUsable(itemStack)) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isTouchingWaterOrRain()) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    public boolean isSuitableFor(BlockState state) {
        return state.isIn(BirthdayBlockTagGenerator.NEEDS_TOOL_LEVEL_5);
    }

    public String getTranslationKey(ItemStack stack) {
        if (BlockItem.getBlockEntityNbt(stack) != null) {
            String var10000 = this.getTranslationKey();
            return var10000 + "." + getColor(stack).getName();
        } else {
            return super.getTranslationKey(stack);
        }
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
        tooltip.add(new LiteralText("press Throw (default R) while blocking to release."));
    }

    @Override
    public PersistentProjectileEntity getProjectileEntity(World world, PlayerEntity playerEntity, ItemStack stack) {
        return new CapShieldEntity(world, playerEntity, stack);
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

    public static DyeColor getColor(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        return nbtCompound != null ? DyeColor.byId(nbtCompound.getInt("Base")) : DyeColor.WHITE;
    }

}
