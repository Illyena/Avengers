package illyena.gilding.core.item.util;

import illyena.gilding.GildingInit;
import illyena.gilding.avengers.AvengersInit;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;


public class GildingItemGroups {
    public static void callGildingItemGroups() {
        GildingInit.LOGGER.info("Registering Item Groups for " + GildingInit.SUPER_MOD_NAME + " Mod.");
    }

    private static ItemGroup registerItemGroup(String modId, String name, Item item) {
//        LangJson.setItemGroupLang(modId, name);
        return FabricItemGroupBuilder.build(new Identifier(modId, name), () -> new ItemStack(item));
    }

//    public static final ItemGroup GILDED = registerItemGroup(GildedInitializer.MOD_ID, "gilded_group", Items.GOLD_NUGGET);

//    public static final ItemGroup ICE_GROUP = registerItemGroup(IcingInitializer.MOD_ID, "icing_group", Items.ICE);

//    public static final ItemGroup VERDURE = registerItemGroup(VerdureInitializer.MOD_ID, "verdure_group", Items.FLOWERING_AZALEA);

    public static final ItemGroup AVENGERS = registerItemGroup(AvengersInit.MOD_ID, "avengers_group", Items.CAKE);

}
