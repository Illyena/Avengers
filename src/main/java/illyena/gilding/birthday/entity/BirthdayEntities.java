package illyena.gilding.birthday.entity;

import illyena.gilding.birthday.entity.projectile.CapShieldEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayEntities {
    public static void registerRobertEntities() {
        LOGGER.info("Registering entities for " + MOD_NAME + " Mod.");
    }

    private static <T extends Entity> EntityType<T> registerPersistentProjectile(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, id), builder.trackRangeBlocks(4).trackedUpdateRate(20).build());
    }

    public static final EntityType<CapShieldEntity> CAP_SHIELD_ENTITY_TYPE = registerPersistentProjectile("cap_shield_entity",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, CapShieldEntity::new));

}
