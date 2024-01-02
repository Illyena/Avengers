package illyena.gilding.avengers.structure;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersStructures {
    public static StructureType<StarLabStructure> STAR_LAB;
    public static RegistryKey<Structure> STAR_LAB_STRUCTURE;

    public static void registerStructures() {
        LOGGER.info("Registering structures for {} mod.", MOD_NAME);

        STAR_LAB = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(MOD_ID, "star_lab"), () -> StarLabStructure.CODEC);
        STAR_LAB_STRUCTURE = RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(MOD_ID, "star_lab"));
    }

}
