package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.structure.AvengersStructures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

import java.util.concurrent.CompletableFuture;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersStructureTagGenerator extends FabricTagProvider<Structure> {
    public static final TagKey<Structure> STAR_PORTAL_TELEPORTS_TO = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(MOD_ID, "star_portal_teleports_to"));

    public AvengersStructureTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.STRUCTURE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(STAR_PORTAL_TELEPORTS_TO).addOptional(AvengersStructures.STAR_LAB_STRUCTURE);
    }

}
