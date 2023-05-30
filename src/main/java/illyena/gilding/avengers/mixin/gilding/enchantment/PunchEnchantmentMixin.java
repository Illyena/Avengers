package illyena.gilding.avengers.mixin.gilding.enchantment;

import illyena.gilding.core.item.IThrowable;
import net.minecraft.enchantment.PunchEnchantment;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PunchEnchantment.class)
public class PunchEnchantmentMixin {

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof IThrowable || stack.getItem() instanceof BowItem;
    }
}
