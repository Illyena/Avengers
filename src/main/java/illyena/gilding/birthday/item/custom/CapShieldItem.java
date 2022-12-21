package illyena.gilding.birthday.item.custom;

//import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShield;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import illyena.gilding.birthday.entity.projectile.CapShieldEntity;
import illyena.gilding.core.item.IThrowable;
import illyena.gilding.core.item.util.GildingToolMaterials;
import illyena.gilding.core.util.data.GildingBlockTagGenerator;
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
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapShieldItem extends MiningToolItem implements IThrowable {
    public CapShieldItem(FabricItemSettings settings) {
        super(3, -2.0f, GildingToolMaterials.MAGIC, GildingBlockTagGenerator.MAGIC_MINEABLE, settings);
        //shield
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
        //trident
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9000000953674316, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        //fabricShieldLib
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f;
            });
        }
    }

    public UseAction getUseAction(ItemStack stack) { return UseAction.BLOCK; }

    public int getMaxUseTime(ItemStack stack) { return 72000; }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        //trident
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isTouchingWaterOrRain()) {
            return TypedActionResult.fail(itemStack);
        } else {
        //shield
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }
    /** SHIELD CODE */

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
        tooltip.add(Text.translatable("press Throw (default R) while blocking to release."));
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(GildingBlockTagGenerator.MAGIC_MINEABLE) ? this.miningSpeed + 6.0f : 1.0f;
    }

    public static DyeColor getColor(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        return nbtCompound != null ? DyeColor.byId(nbtCompound.getInt("Base")) : DyeColor.WHITE;
    }

    /** Trident Code */

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> {
            e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if ((double)state.getHardness(world, pos) != 0.0) {
            stack.damage(2, miner, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
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

//    @Override
    public int getCoolDownTicks() { return 100; }

//    @Override
    public boolean supportsBanner() { return false; }
}
