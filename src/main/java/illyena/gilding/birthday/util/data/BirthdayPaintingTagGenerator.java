package illyena.gilding.birthday.util.data;

import illyena.gilding.birthday.painting.BirthdayPaintings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.MOD_ID;

public class BirthdayPaintingTagGenerator extends FabricTagProvider<PaintingMotive> {
//    public static final TagKey<PaintingMotive> BIRTHDAY_PAINTINGS = TagKey.of(Registry.PAINTING_MOTIVE, new Identifier(MOD_ID, "placeable"));

    public BirthdayPaintingTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.PAINTING_MOTIVE, "painting/"); }

    @Override
    protected void generateTags() {
 //       getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).addOptionalTag(BIRTHDAY_PAINTINGS);
 //      getOrCreateTagBuilder(BIRTHDAY_PAINTINGS).add(BirthdayPaintings.CAPTAIN);
    }
}
