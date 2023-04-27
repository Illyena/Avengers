package illyena.gilding.birthday.item;

import illyena.gilding.birthday.block.BirthdayBlocks;
import illyena.gilding.birthday.item.custom.CapShieldItem;
import illyena.gilding.core.item.util.GildingItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayItems {

    public static void registerItems() {
        LOGGER.info("Registering items for " + MOD_NAME);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    private static BlockItem registerBlockItem(String name, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(GildingItemGroups.BIRTHDAY)));
    }

    /** Items */

    public static final Item CAP_SHIELD = registerItem("cap_shield",
            new CapShieldItem(new FabricItemSettings().maxDamage(336).fireproof().rarity(Rarity.EPIC).group(GildingItemGroups.BIRTHDAY)));


}
