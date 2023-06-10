package illyena.gilding.avengers.util.data;

import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersTags {
    public static void registerTags() { LOGGER.info("Registering Tags for {}.", MOD_NAME); }

    public static interface StructureTags extends ConfiguredStructureFeatureTags {
        private static TagKey<ConfiguredStructureFeature<?,?>> createTag(String modId, String name) {
            return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(modId, name));
        }
        public static final TagKey<ConfiguredStructureFeature<?,?>> STAR_PORTAL_TELEPORTS_TO = createTag(MOD_ID, "star_portal_teleports_to");
    }

}
