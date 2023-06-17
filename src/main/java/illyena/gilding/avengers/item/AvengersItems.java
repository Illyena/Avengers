package illyena.gilding.avengers.item;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.item.custom.CapShieldItem;
import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersItems {

    public static void registerItems() { LOGGER.info("Registering items for " + MOD_NAME); }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    private static BlockItem registerBlockItem(String name, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(AVENGERS_GROUP)));
    }

    private static Item registerAliasedBlockItem(String name, Block block, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    /** Items */

    public static final Item CAP_SHIELD = registerItem("cap_shield",
            new CapShieldItem(new FabricItemSettings().maxDamage(336).fireproof().rarity(Rarity.EPIC).group(AVENGERS_GROUP)));
    public static final Item MJOLNIR = registerAliasedBlockItem("mjolnir", AvengersBlocks.MJOLNIR_BLOCK,
            new MjolnirItem(AvengersBlocks.MJOLNIR_BLOCK, 8.0f, -2.9f, ToolMaterials.NETHERITE, new FabricItemSettings().maxDamage(336).fireproof().rarity(Rarity.EPIC).group(AVENGERS_GROUP)));

}
