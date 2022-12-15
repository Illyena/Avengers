package illyena.gilding.birthday.structure;

import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayStructures {
    public static StructureType<StarLabStructure> STAR_LAB;

    public static void registerBirthdayStructures() {
        LOGGER.info("Registering Structures for " + MOD_NAME + " Mod.");

        STAR_LAB = Registry.register(Registry.STRUCTURE_TYPE, new Identifier(MOD_ID, "star_lab"), () -> StarLabStructure.CODEC);

    }

}
