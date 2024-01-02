package illyena.gilding.avengers.mixin.enchantment;

import illyena.gilding.avengers.item.custom.MjolnirItem;
import illyena.gilding.core.enchantment.GildingEnchantmentTarget;
import net.minecraft.enchantment.ChannelingEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static illyena.gilding.avengers.config.AvengersConfigOptions.MJOLNIR_LEGACY;

@SuppressWarnings({"unused"})
@Mixin(ChannelingEnchantment.class)
public class ChannelingEnchantmentMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;<init>(Lnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnchantmentTarget;[Lnet/minecraft/entity/EquipmentSlot;)V"), index = 1)
    private static EnchantmentTarget channelingEnchantmentTarget (EnchantmentTarget enchantmentTarget) {
        return GildingEnchantmentTarget.THROWABLE_TARGET;
    }

    @SuppressWarnings("MissingUnique")
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof TridentItem || (stack.getItem() instanceof MjolnirItem && !MJOLNIR_LEGACY.getValue());
    }

}
