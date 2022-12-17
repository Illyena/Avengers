package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * must not be in mixins.json
 */
@Mixin(EnchantmentTarget.class)
public abstract class EnchantmentTargetMixin {
    @Shadow
    public abstract boolean isAcceptableItem(Item item);
}
