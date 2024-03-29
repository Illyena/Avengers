package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.core.util.data.GildingItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersItemTagGenerator extends FabricTagProvider<Item> {
    public static final TagKey<Item> SHIELDS = TagKey.of(Registry.ITEM_KEY, new Identifier(MOD_ID, "shields"));

    public AvengersItemTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.ITEM); }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(GildingItemTagGenerator.SHIELDS).addOptionalTag(SHIELDS);
        getOrCreateTagBuilder(SHIELDS).add(AvengersItems.CAP_SHIELD);
    }
}
