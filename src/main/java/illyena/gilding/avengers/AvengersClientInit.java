package illyena.gilding.avengers;

import illyena.gilding.avengers.client.render.AvengersRenderers;
import illyena.gilding.avengers.client.render.entity.model.BirthdayEntityModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AvengersClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        BirthdayEntityModelLayers.registerModelLayers();
        AvengersRenderers.registerRenderers();

    }

}
