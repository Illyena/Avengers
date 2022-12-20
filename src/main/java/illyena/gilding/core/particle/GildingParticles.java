package illyena.gilding.core.particle;

import illyena.gilding.core.particle.custom.StarParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.GildingInit.*;

public class GildingParticles {
    public static void callGildingParticles() {
        LOGGER.info("Registering Blocks for " + SUPER_MOD_NAME + " Mod.");
    }

    private static DefaultParticleType registerParticle(String name, DefaultParticleType particle) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(SUPER_MOD_ID, name), particle);
    }

    public static final DefaultParticleType STAR_PARTICLE = registerParticle("star_particle", FabricParticleTypes.simple());

    @Environment(EnvType.CLIENT)
    public static void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(GildingParticles.STAR_PARTICLE, StarParticle.Factory::new);
    }
}
