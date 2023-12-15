package illyena.gilding.avengers.client.render.entity.model;

import illyena.gilding.avengers.client.render.blockentity.StarPortalBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

@Environment(EnvType.CLIENT)
public class AvengersEntityModelLayers {
    public static final EntityModelLayer CAP_SHIELD_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "cap_shield_model_layer"), "main");
    public static final EntityModelLayer MJOLNIR_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "mjolnir_model_layer"), "main");
    public static final EntityModelLayer STAR_PORTAL_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "star_portal_model"), "main");

    public static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(CAP_SHIELD_MODEL_LAYER, CapShieldEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MJOLNIR_MODEL_LAYER, MjolnirEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(STAR_PORTAL_MODEL_LAYER, StarPortalBlockEntityRenderer.StarPortalModel::getTexturedModelData);
    }

}
