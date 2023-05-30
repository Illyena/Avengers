package illyena.gilding;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable{

    @Override
    public void run() {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String enchantmentTarget = mappingResolver.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("THROWABLE", "illyena.gilding.core.enchantment.ThrowableTarget").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("PROJECTILE", "illyena.gilding.core.enchantment.ProjectileTarget").build();

    }
}
