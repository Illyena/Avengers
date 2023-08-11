package illyena.gilding.avengers;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.config.AvengersConfigOptions;
import illyena.gilding.avengers.entity.AvengersEntities;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.avengers.painting.AvengersPaintings;
import illyena.gilding.avengers.structure.AvengersStructures;
import illyena.gilding.compat.Mod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static illyena.gilding.GildingInit.GILDING;

public class AvengersInit implements ModInitializer {
    public static final String MOD_ID = "avengers";
    public static final String MOD_NAME = "Avengers Assemble!";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final Mod AVENGERS = new Mod(MOD_ID, GILDING, false, AvengersConfigOptions.class);
    public static final ItemGroup AVENGERS_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "avengers_group"),
            FabricItemGroup.builder().displayName(translationKeyOf("item_group", "avengers_group"))
                    .icon(() -> new ItemStack(AvengersItems.CAP_SHIELD)).build());

    public void onInitialize() {
        AvengersConfigOptions.registerConfigs();
        LOGGER.info("Happy Birthday Papa!");

        AvengersBlocks.registerBlocks();
        AvengersBlockEntities.registerBlockEntities();
        AvengersItems.registerItems();
        AvengersEntities.registerEntities();
        AvengersStructures.registerStructures();
        AvengersPaintings.callPaintings();
    }

    public static Text translationKeyOf(String type, String key) {
        return Text.translatable(type + "." + MOD_ID + "." + key);
    }
}
