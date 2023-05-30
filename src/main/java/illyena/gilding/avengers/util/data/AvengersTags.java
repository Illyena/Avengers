package illyena.gilding.avengers.util.data;

import net.minecraft.tag.StructureTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersTags {
    public static void registerTags() { LOGGER.info("Registering Tags for {}.", MOD_NAME); }

    public static interface AvengersStructureTags extends StructureTags {
        public static final TagKey<Structure> STAR_PORTAL_TELEPORTS_TO = TagKey.of(Registry.STRUCTURE_KEY, new Identifier(MOD_ID, "star_portal_teleports_to"));
    }

}