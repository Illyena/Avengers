package illyena.gilding.mixin.gilding.enchantment;

import illyena.gilding.core.enchantment.GildingEnchantmentTarget;
import illyena.gilding.core.item.IThrowable;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.PunchEnchantment;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PunchEnchantment.class)
public class PunchEnchantmentMixin {

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof IThrowable || stack.getItem() instanceof BowItem;
    }
}
