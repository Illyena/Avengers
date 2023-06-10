package illyena.gilding.avengers.client.render;

import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.client.render.blockentity.StarPortalBlockEntityRenderer;
import illyena.gilding.avengers.client.render.blockentity.TeleportAnchorBlockEntityRenderer;
import illyena.gilding.avengers.client.render.entity.CapShieldEntityRenderer;
import illyena.gilding.avengers.client.render.entity.MjolnirEntityRenderer;
import illyena.gilding.avengers.entity.AvengersEntities;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;
import static illyena.gilding.avengers.block.AvengersBlocks.MJOLNIR_BLOCK;

public class AvengersRenderers {

    public static void registerRenderers() {
        LOGGER.info("Registering Renderers for " + MOD_NAME + ".");

        EntityRendererRegistry.register(AvengersEntities.CAP_SHIELD_ENTITY_TYPE, CapShieldEntityRenderer::new);
        EntityRendererRegistry.register(AvengersEntities.MJOLNIR_ENTITY_TYPE, MjolnirEntityRenderer::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer) {
//                registrationHelper.register(new MjolnirChargingFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, context.getModelLoader()));
//                registrationHelper.register(new MjolnirChargingFeatureRenderer<>((PlayerEntityRenderer)entityRenderer, context));
            }
        });
//        EntityModelLayerRegistry.registerModelLayer(MJOLNIR_CHARGING_MODEL_LAYER, MjolnirChargingFeatureRenderer::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlock(MJOLNIR_BLOCK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(STAR_PORTAL_MODEL_LAYER, StarPortalBlockEntityRenderer.StarPortalModel::getTexturedModelData);

        BlockEntityRendererRegistry.register(AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, TeleportAnchorBlockEntityRenderer::new);

    }

    public static final EntityModelLayer STAR_PORTAL_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "star_portal_model"), "main");
    public static final EntityModelLayer MJOLNIR_CHARGING_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "mjolnir_charging_model"), "main");

}
