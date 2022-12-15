package illyena.gilding.core.enchantment;

import illyena.gilding.core.item.IThrowable;
import illyena.gilding.mixin.gilding.enchantment.EnchantmentTargetMixin;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;

public class ProjectileTarget extends EnchantmentTargetMixin {
    @Override
    public boolean isAcceptableItem(Item item) { return item instanceof IThrowable || item instanceof BowItem; }
}
