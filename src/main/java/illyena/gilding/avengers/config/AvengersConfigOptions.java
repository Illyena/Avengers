package illyena.gilding.avengers.config;

import illyena.gilding.config.option.IntegerConfigOption;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersConfigOptions {
    public static final IntegerConfigOption STAR_LAB_CONFIG = new IntegerConfigOption(MOD_ID, "star_lab_config", 3, 1, 5);

    public static void registerConfigs() { }

}