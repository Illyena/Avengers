package illyena.gilding.avengers.mixin.enchantment;

import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.minecraft.enchantment.RiptideEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RiptideEnchantment.class)
public class RiptideEnchantmentMixin {

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof TridentItem || stack.getItem() instanceof MjolnirItem;
    }

}