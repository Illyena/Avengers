package illyena.gilding.birthday;

import illyena.gilding.birthday.client.render.BirthdayRenderers;
import illyena.gilding.birthday.client.render.entity.model.BirthdayEntityModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BirthdayClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        BirthdayEntityModelLayers.registerModelLayers();
        BirthdayRenderers.registerRenderers();

    }

}
