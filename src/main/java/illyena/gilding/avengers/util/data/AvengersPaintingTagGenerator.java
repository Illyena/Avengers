package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.painting.AvengersPaintings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersPaintingTagGenerator extends FabricTagProvider<PaintingVariant> {
    public static final TagKey<PaintingVariant> AVENGERS_PAINTINGS = TagKey.of(RegistryKeys.PAINTING_VARIANT, new Identifier(MOD_ID, "placeable"));

    public AvengersPaintingTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.PAINTING_VARIANT, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).addOptionalTag(AVENGERS_PAINTINGS);
        getOrCreateTagBuilder(AVENGERS_PAINTINGS).add(AvengersPaintings.CAPTAIN);
    }
}
