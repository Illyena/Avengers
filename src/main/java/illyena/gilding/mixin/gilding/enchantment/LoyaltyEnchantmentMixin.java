package illyena.gilding.mixin.gilding.enchantment;

import illyena.gilding.core.enchantment.GildingEnchantmentTarget;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.LoyaltyEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoyaltyEnchantment.class)
public class LoyaltyEnchantmentMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;<init>(Lnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnchantmentTarget;[Lnet/minecraft/entity/EquipmentSlot;)V"), index = 1)
    private static EnchantmentTarget loyaltyEnchantmentTarget (EnchantmentTarget enchantmentTarget) {
        return GildingEnchantmentTarget.THROWABLE_TARGET;
    }

}
