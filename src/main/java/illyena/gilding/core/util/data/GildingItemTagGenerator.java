package illyena.gilding.core.util.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class GildingItemTagGenerator extends FabricTagProvider<Item> {
    public static final TagKey<Item> SHIELDS = TagKey.of(Registry.ITEM_KEY, new Identifier(SUPER_MOD_ID, "shields"));

    public GildingItemTagGenerator(FabricDataGenerator dataGenerator) { super(dataGenerator, Registry.ITEM, "item/"); }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(SHIELDS);
    }
}
