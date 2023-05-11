package illyena.gilding.avengers;

import illyena.gilding.avengers.client.gui.screen.AvengersConfigMenu;
import illyena.gilding.avengers.client.render.AvengersRenderers;
import illyena.gilding.avengers.client.render.entity.model.AvengersEntityModelLayers;
import illyena.gilding.compat.Mod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

@Environment(EnvType.CLIENT)
public class AvengersClientInit implements ClientModInitializer {
    public static final Screen AVENGERS_CONFIG_SCREEN = Mod.ModScreens.registerConfigScreen(MOD_ID, new AvengersConfigMenu());

    @Override
    public void onInitializeClient() {
        AvengersEntityModelLayers.registerModelLayers();
        AvengersRenderers.registerRenderers();

    }

}
