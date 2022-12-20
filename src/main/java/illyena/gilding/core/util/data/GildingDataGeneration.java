package illyena.gilding.core.util.data;

import illyena.gilding.birthday.util.data.BirthdayLootTableProvider;
import illyena.gilding.birthday.util.data.BirthdayModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GildingDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(BirthdayLootTableProvider::new);
        fabricDataGenerator.addProvider(BirthdayModelProvider::new);
    }
}
