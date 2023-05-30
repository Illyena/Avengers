package illyena.gilding.avengers.structure;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersStructures {
    public static StructureType<StarLabStructure> STAR_LAB;

    public static void registerStructures() {
        LOGGER.info("Registering Structures for {}", MOD_NAME);

        STAR_LAB = Registry.register(Registry.STRUCTURE_TYPE, new Identifier(MOD_ID, "star_lab"), () -> StarLabStructure.CODEC);

    }

}
