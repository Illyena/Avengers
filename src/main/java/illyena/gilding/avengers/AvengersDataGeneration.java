package illyena.gilding.avengers;

import illyena.gilding.avengers.util.data.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AvengersDataGeneration implements DataGeneratorEntrypoint{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(AvengersModelProvider::new);
        fabricDataGenerator.addProvider(AvengersLootTableProvider::new);
        fabricDataGenerator.addProvider(AvengersBlockTagGenerator::new);
        fabricDataGenerator.addProvider(AvengersItemTagGenerator::new);
        fabricDataGenerator.addProvider(AvengersPaintingTagGenerator::new);
    }
}
