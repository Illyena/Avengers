package illyena.gilding.avengers.entity;

import illyena.gilding.avengers.entity.projectile.CapShieldEntity;
import illyena.gilding.avengers.entity.projectile.MjolnirEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersEntities {
    public static void registerEntities() { LOGGER.info("Registering entities for {} mod.", MOD_NAME); }

    private static <T extends Entity> EntityType<T> registerPersistentProjectile(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(MOD_ID, id), builder.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());
    }

    public static final EntityType<CapShieldEntity> CAP_SHIELD_ENTITY_TYPE = registerPersistentProjectile("cap_shield_entity",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, CapShieldEntity::new));
    public static final EntityType<MjolnirEntity> MJOLNIR_ENTITY_TYPE = registerPersistentProjectile("mjolnir_entity",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, MjolnirEntity::new));

}
