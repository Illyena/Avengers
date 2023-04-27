package illyena.gilding.core.util;

import illyena.gilding.GildingInit;
import illyena.gilding.birthday.BirthdayInitializer;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import static illyena.gilding.GildingInit.SUPER_MOD_NAME;

public class GildingTags {
    public static void callGildingTags() {
        GildingInit.LOGGER.info("Registering Tags for " + SUPER_MOD_NAME + " Mod.");
    }

    public static interface GildingStructureTags extends ConfiguredStructureFeatureTags {
        private static TagKey<ConfiguredStructureFeature<?,?>> createTag(String modId, String name) {
            return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(modId, name));
        }

        public static final TagKey<ConfiguredStructureFeature<?,?>> STAR_PORTAL_TELEPORTS_TO = createTag("minecraft", "star_portal_teleports_to");


    }
}
