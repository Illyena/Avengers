package illyena.gilding.birthday;

import illyena.gilding.birthday.block.BirthdayBlocks;
import illyena.gilding.birthday.block.blockentity.BirthdayBlockEntities;
import illyena.gilding.birthday.entity.BirthdayEntities;
import illyena.gilding.birthday.item.BirthdayItems;
import illyena.gilding.birthday.painting.BirthdayPaintings;
import illyena.gilding.birthday.structure.BirthdayStructures;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BirthdayInitializer implements ModInitializer {
    public static final String MOD_ID = "birthday";
    public static final String MOD_NAME = "Happy Birthday!";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public void onInitialize() {
        LOGGER.info("Happy Birthday Papa!");

        BirthdayBlocks.callBirthdayBlocks();
        BirthdayBlockEntities.registerBirthdayBlockEntities();
        BirthdayItems.registerItems();
        BirthdayEntities.registerRobertEntities();
        BirthdayStructures.registerBirthdayStructures();
        BirthdayPaintings.callPaintings();


    }
}
