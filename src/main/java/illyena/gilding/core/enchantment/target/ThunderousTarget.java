package illyena.gilding.core.enchantment.target;

import illyena.gilding.core.enchantment.EnchantmentTargetMixin;
import illyena.gilding.core.item.IThunderous;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
public class ThunderousTarget extends EnchantmentTargetMixin {

    public boolean isAcceptableItem(Item item) { return item instanceof IThunderous; }

    public boolean method_8177(Item item) { return item instanceof IThunderous; }

}
