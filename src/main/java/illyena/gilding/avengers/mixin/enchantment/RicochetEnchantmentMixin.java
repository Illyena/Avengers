package illyena.gilding.avengers.mixin.enchantment;

import illyena.gilding.avengers.item.custom.MjolnirItem;
import illyena.gilding.core.enchantment.RicochetEnchantment;
import illyena.gilding.core.item.IThrowable;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(RicochetEnchantment.class)
public class RicochetEnchantmentMixin {

    @SuppressWarnings("MissingUnique")
    public boolean isAcceptableItem(ItemStack stack) {
        return !(stack.getItem() instanceof MjolnirItem) && stack.getItem() instanceof IThrowable;
    }

}
