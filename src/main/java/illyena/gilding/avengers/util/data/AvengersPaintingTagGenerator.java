package illyena.gilding.avengers.util.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.registry.Registry;

public class AvengersPaintingTagGenerator extends FabricTagProvider<PaintingMotive> {
//    public static final TagKey<PaintingMotive> BIRTHDAY_PAINTINGS = TagKey.of(Registry.PAINTING_MOTIVE, new Identifier(MOD_ID, "placeable"));

    public AvengersPaintingTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.PAINTING_MOTIVE, "painting/"); }

    @Override
    protected void generateTags() {
 //       getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).addOptionalTag(BIRTHDAY_PAINTINGS);
 //      getOrCreateTagBuilder(BIRTHDAY_PAINTINGS).add(BirthdayPaintings.CAPTAIN);
    }
}
