package illyena.gilding.core.enchantment;

import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.enchantment.EnchantmentTarget;

public class GildingEnchantmentTarget {

    public static final EnchantmentTarget THROWABLE_TARGET = ClassTinkerers.getEnum(EnchantmentTarget.class, "THROWABLE");
    public static final EnchantmentTarget PROJECTILE_TARGET = ClassTinkerers.getEnum(EnchantmentTarget.class, "PROJECTILE");
}
