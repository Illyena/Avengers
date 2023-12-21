package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.structure.AvengersStructures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersStructureTagGenerator extends FabricTagProvider.DynamicRegistryTagProvider<Structure> {
    public static final TagKey<Structure> STAR_PORTAL_TELEPORTS_TO = TagKey.of(Registry.STRUCTURE_KEY, new Identifier(MOD_ID, "star_portal_teleports_to"));

    public AvengersStructureTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.STRUCTURE_KEY); }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(STAR_PORTAL_TELEPORTS_TO).addOptional(AvengersStructures.STAR_LAB_STRUCTURE.getValue());
    }

}
