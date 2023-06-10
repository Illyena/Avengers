package illyena.gilding.avengers.structure;

import illyena.gilding.mixin.worldgen.StructureFeatureAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;


import java.util.Map;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersStructures {
    public static StructureFeature<StructurePoolFeatureConfig> STAR_LAB = new StarLabStructure();
    public static RegistryEntry<ConfiguredStructureFeature<?, ?>> STAR_LAB_CONFIG;
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> STAR_LAB_CONFIG_KEY;

    public static void registerStructures() {
        LOGGER.info("Registering Structures for " + MOD_NAME + " Mod.");

        StructureFeatureAccessor.callRegister(MOD_ID + ":star_lab", STAR_LAB, GenerationStep.Feature.RAW_GENERATION);

        STAR_LAB_CONFIG_KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(MOD_ID, "star_lab"));

        STAR_LAB_CONFIG = register(STAR_LAB_CONFIG_KEY, STAR_LAB.configure(new StructurePoolFeatureConfig(
                StarLabGenerator.STRUCTURE_POOLS, 7), BiomeTags.END_CITY_HAS_STRUCTURE, true,
                Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE,
                        Pool.of(new SpawnSettings.SpawnEntry(EntityType.SHULKER, 1, 1, 1))))));

    }

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<?, ?>> register(RegistryKey<ConfiguredStructureFeature<?, ?>> key, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, key, configuredStructureFeature);
    }

}
