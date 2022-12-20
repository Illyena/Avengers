package illyena.gilding;

//import illyena.gilding.gilded.GildedClientInitializer;
//import illyena.gilding.verdure.VerdureClientInitializer;
import illyena.gilding.core.event.KeyInputHandler;
import illyena.gilding.core.networking.GildingPackets;
import illyena.gilding.core.particle.GildingParticles;
import illyena.gilding.core.particle.custom.StarParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.PortalParticle;

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

        GildingParticles.registerParticles();

        GildingPackets.registerS2CPackets();
        KeyInputHandler.register();
    }
}
