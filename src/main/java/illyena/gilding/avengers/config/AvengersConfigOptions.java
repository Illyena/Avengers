package illyena.gilding.avengers.config;

import illyena.gilding.compat.Mod.Configs;
import illyena.gilding.config.option.BooleanConfigOption;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersConfigOptions implements Configs {
    public static final IntegerConfigOption STAR_LABS = new IntegerConfigOption(MOD_ID, "star_lab_config", 3, 1, 5, ConfigOption.AccessType.WORLD_GEN);
    public static final BooleanConfigOption MJOLNIR_LEGACY = new BooleanConfigOption(MOD_ID, "mjolnir_legacy_config", true, ConfigOption.AccessType.BOTH);

}
