package illyena.gilding.avengers.client.render;

import illyena.gilding.avengers.block.AvengersBlocks;
import illyena.gilding.avengers.block.blockentity.AvengersBlockEntities;
import illyena.gilding.avengers.client.render.blockentity.StarPortalBlockEntityRenderer;
import illyena.gilding.avengers.client.render.blockentity.TeleportAnchorBlockEntityRenderer;
import illyena.gilding.avengers.client.render.entity.CapShieldEntityRenderer;
import illyena.gilding.avengers.client.render.entity.MjolnirEntityRenderer;
import illyena.gilding.avengers.entity.AvengersEntities;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import static illyena.gilding.avengers.AvengersInit.LOGGER;
import static illyena.gilding.avengers.AvengersInit.MOD_NAME;

public class AvengersRenderers {

    public static void registerRenderers() {
        LOGGER.info("Registering renderers for {} mod.", MOD_NAME);

        EntityRendererRegistry.register(AvengersEntities.CAP_SHIELD_ENTITY_TYPE, CapShieldEntityRenderer::new);
        EntityRendererRegistry.register(AvengersEntities.MJOLNIR_ENTITY_TYPE, MjolnirEntityRenderer::new);

        BlockEntityRendererFactories.register(AvengersBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, TeleportAnchorBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(AvengersBlocks.MJOLNIR_BLOCK, RenderLayer.getCutout());
    }

}
