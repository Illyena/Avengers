package illyena.gilding.core.enchantment;

import illyena.gilding.core.item.IThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;

public class ThrowableTarget extends EnchantmentTargetMixin {

    public boolean isAcceptableItem(Item item) { return item instanceof IThrowable || item instanceof TridentItem; }

    public boolean method_8177(Item item) { return item instanceof IThrowable || item instanceof TridentItem; }
}
