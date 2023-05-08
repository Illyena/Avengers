package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.GildingInit.*;

public class GildingEnchantments {

    public static void callEnchantments() {
        LOGGER.info("Registering enchantments for " + SUPER_MOD_NAME);
    }

    public static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(SUPER_MOD_ID, name), enchantment);
    }

    public static final Enchantment RICOCHET = registerEnchantment("ricochet",
            new RicochetEnchantment(Enchantment.Rarity.RARE, GildingEnchantmentTarget.THROWABLE_TARGET, EquipmentSlot.MAINHAND));

}
