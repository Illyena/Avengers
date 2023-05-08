package illyena.gilding.avengers;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.config.AvengersConfigOptions;
import illyena.gilding.avengers.entity.AvengersEntities;
import illyena.gilding.avengers.event.AvengersEvents;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.avengers.painting.AvengersPaintings;
import illyena.gilding.avengers.structure.AvengersStructures;
import illyena.gilding.avengers.util.data.AvengersTags;
import illyena.gilding.compat.Mod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static illyena.gilding.GildingInit.GILDING;
import static illyena.gilding.avengers.item.AvengersItems.CAP_SHIELD;

public class AvengersInit implements ModInitializer {
    public static final String MOD_ID = "avengers";
    public static final String MOD_NAME = "Avengers Assemble!";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final Mod AVENGERS = new Mod(MOD_ID, GILDING, false, AvengersConfigOptions.class);

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "avengers_group"), () -> new ItemStack(CAP_SHIELD));

    public void onInitialize() {
        AvengersConfigOptions.registerConfigs();
        LOGGER.info("Happy Birthday Papa!");

        AvengersBlocks.callAvengersBlocks();
        AvengersBlockEntities.registerAvengersBlockEntities();
        AvengersItems.registerItems();
        AvengersEntities.registerRobertEntities();
        AvengersStructures.registerAvengersStructures();
        AvengersPaintings.callPaintings();
        AvengersEvents.registerEvents();
        AvengersTags.callAvengersTags();

    }
}
