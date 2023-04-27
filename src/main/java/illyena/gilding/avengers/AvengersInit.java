package illyena.gilding.avengers;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.entity.AvengersEntities;
import illyena.gilding.avengers.event.AvengersEvents;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.avengers.painting.AvengersPaintings;
import illyena.gilding.avengers.structure.AvengersStructures;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AvengersInit implements ModInitializer {
    public static final String MOD_ID = "avengers";
    public static final String MOD_NAME = "Avengers Assemble!";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public void onInitialize() {
        LOGGER.info("Happy Birthday Papa!");

        AvengersBlocks.callAvengersBlocks();
        AvengersBlockEntities.registerAvengersBlockEntities();
        AvengersItems.registerItems();
        AvengersEntities.registerRobertEntities();
        AvengersStructures.registerAvengersStructures();
        AvengersPaintings.callPaintings();
        AvengersEvents.registerEvents();


    }
}
