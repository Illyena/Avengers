package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class GildingEnchantmentHelper extends EnchantmentHelper {

    public static boolean hasRicochet(ItemStack stack) {
        return getLevel(GildingEnchantments.RICOCHET, stack) > 0;
    }

    public static int getRicochet(ItemStack stack) { return getLevel(GildingEnchantments.RICOCHET, stack); }

}
