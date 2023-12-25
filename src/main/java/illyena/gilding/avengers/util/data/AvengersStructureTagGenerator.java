package illyena.gilding.avengers.util.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;
import static illyena.gilding.avengers.structure.AvengersStructures.STAR_LAB_CONFIG_KEY;

public class AvengersStructureTagGenerator extends FabricTagProvider.DynamicRegistryTagProvider<ConfiguredStructureFeature<?,?>> {
    public static final TagKey<ConfiguredStructureFeature<?,?>> STAR_PORTAL_TELEPORTS_TO = TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(MOD_ID, "star_portal_teleports_to"));

    public AvengersStructureTagGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, "/worldgen/configured_structure_feature","");
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(STAR_PORTAL_TELEPORTS_TO).addOptional(STAR_LAB_CONFIG_KEY);
    }

}
