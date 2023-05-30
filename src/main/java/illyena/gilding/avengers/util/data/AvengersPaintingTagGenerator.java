package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.painting.AvengersPaintings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.tag.PaintingVariantTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersPaintingTagGenerator extends FabricTagProvider<PaintingVariant> {
    public static final TagKey<PaintingVariant> AVENGERS_PAINTINGS = TagKey.of(Registry.PAINTING_VARIANT_KEY, new Identifier(MOD_ID, "placeable"));

    public AvengersPaintingTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.PAINTING_VARIANT); }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).addOptionalTag(AVENGERS_PAINTINGS);
        getOrCreateTagBuilder(AVENGERS_PAINTINGS).add(AvengersPaintings.CAPTAIN);
    }
}
