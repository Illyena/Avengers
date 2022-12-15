package illyena.gilding.birthday.block;

import illyena.gilding.core.item.util.GildingItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayBlocks {
    public static void callBirthdayBlocks() {
        LOGGER.info("Registering Blocks for " + MOD_NAME + " Mod.");
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), new BlockItem(block, new FabricItemSettings().group(GildingItemGroups.BIRTHDAY)));
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    //BLOCKS

    public static final Block STAR_PORTAL_BLOCK = registerBlock("star_portal_block", //todo check minability pick tool required
            new StarPortalBlock( FabricBlockSettings.of(Material.SHULKER_BOX, MapColor.BLACK).dynamicBounds().nonOpaque().requiresTool().luminance((state) -> { return  15; })));
    //todo lang, and block assests  item models

    public static final Block TELEPORT_ANCHOR = registerBlock("teleport_anchor", //todo check hardness and explodability pick tool required
            new TeleportAnchorBlock(FabricBlockSettings.of(Material.AIR, MapColor.CLEAR).nonOpaque().requiresTool().strength(50.0F, 1200.0F)));
    //todo lang, and block assests  item models

}
