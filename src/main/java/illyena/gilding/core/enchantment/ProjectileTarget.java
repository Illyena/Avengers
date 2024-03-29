package illyena.gilding.core.enchantment;

import illyena.gilding.core.enchantment.EnchantmentTargetMixin;
import illyena.gilding.core.item.IThrowable;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;

public class ProjectileTarget extends EnchantmentTargetMixin {

    public boolean isAcceptableItem(Item item) { return item instanceof IThrowable || item instanceof BowItem; }

    public boolean method_8177(Item item) { return item instanceof IThrowable || item instanceof BowItem; }
}
