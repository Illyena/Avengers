package illyena.gilding.avengers.util.data;

import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.core.util.data.GildingItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class AvengersItemTagGenerator extends FabricTagProvider<Item> {
    public static final TagKey<Item> SHIELDS = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "shields"));

    public AvengersItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(GildingItemTagGenerator.SHIELDS).addOptionalTag(SHIELDS);
        getOrCreateTagBuilder(SHIELDS).add(AvengersItems.CAP_SHIELD);
    }
}
