package illyena.gilding;

//import illyena.gilding.gilded.GildedClientInitializer;
//import illyena.gilding.verdure.VerdureClientInitializer;
import illyena.gilding.core.event.KeyInputHandler;
import illyena.gilding.core.networking.GildingPackets;
import net.fabricmc.api.ClientModInitializer;

public class GildingClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
//        GildedClientInitializer.initializeGildedClient();
//        IcingClientInitializer.initializeIcingClient();
//        VerdureClientInitializer.initializeVerdureClient();

        // todo FOR TESTING
/**
 * for testing resource pack creation
 * remove prior to use
 */
 //       GildingPreInit.RESOURCE_PACK.dump();

        GildingPackets.registerS2CPackets();
        KeyInputHandler.register();
    }
}
