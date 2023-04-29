package illyena.gilding.avengers.client.render;

import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.client.render.blockentity.StarPortalBlockEntityRenderer;
import illyena.gilding.avengers.client.render.blockentity.TeleportAnchorBlockEntityRenderer;
import illyena.gilding.avengers.client.render.entity.CapShieldEntityRenderer;
import illyena.gilding.avengers.entity.AvengersEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;

public class AvengersRenderers {
    public static void registerRenderers() {
        LOGGER.info("Registering Renderers for " + MOD_NAME + ".");

        EntityRendererRegistry.register(AvengersEntities.CAP_SHIELD_ENTITY_TYPE, CapShieldEntityRenderer::new);

        BlockEntityRendererRegistry.register(AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(STAR_PORTAL_MODEL_LAYER, StarPortalBlockEntityRenderer.StarPortalModel::getTexturedModelData);

        BlockEntityRendererRegistry.register(AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, TeleportAnchorBlockEntityRenderer::new);

    }

    public static final EntityModelLayer STAR_PORTAL_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "star_portal_model"), "main");

}
