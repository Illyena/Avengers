package illyena.gilding.core.util;

import illyena.gilding.GildingInit;
import illyena.gilding.birthday.BirthdayInitializer;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.StructureTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.SUPER_MOD_NAME;

public class GildingTags {
    public static void callGildingTags() {
        GildingInit.LOGGER.info("Registering Tags for " + SUPER_MOD_NAME + " Mod.");
    }

    public static interface GildingStructureTags extends StructureTags {
        private static TagKey<Structure> createTag(String modId, String name) {
            return TagKey.of(Registry.STRUCTURE_KEY, new Identifier(modId, name));
        }

        private static TagKey<Structure> createCommonTag(String name) {
            return TagKey.of(Registry.STRUCTURE_KEY, new Identifier("c", name));
        }

        public static final TagKey<Structure> STAR_PORTAL_TELEPORTS_TO = createTag(BirthdayInitializer.MOD_ID, "star_portal_teleports_to");



    }
}
