package illyena.gilding.avengers.item;

import illyena.gilding.avengers.item.custom.CapShieldItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersItems {

    public static void registerItems() { LOGGER.info("Registering items for " + MOD_NAME); }

    private static Item registerItem(String name, Item item, ItemGroup group) {
        Registries.ITEM_GROUP.getKey(group).ifPresent(key -> ItemGroupEvents.modifyEntriesEvent(key).register(content -> content.add(item)));
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }

    private static BlockItem registerBlockItem(String name, Block block, ItemGroup group) {
        BlockItem item = Registry.register(Registries.ITEM, new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
        Registries.ITEM_GROUP.getKey(group).ifPresent(key -> ItemGroupEvents.modifyEntriesEvent(key).register(content -> content.add(item)));
        return item;
    }

    /** Items */

    public static final Item CAP_SHIELD = registerItem("cap_shield",
            new CapShieldItem(new FabricItemSettings().maxDamage(336).fireproof().rarity(Rarity.EPIC)), AVENGERS_GROUP);


}
