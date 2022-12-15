package illyena.gilding.birthday.client.render;

import illyena.gilding.birthday.block.blockentity.BirthdayBlockEntities;
import illyena.gilding.birthday.client.render.blockentity.StarPortalBlockEntityRenderer;
import illyena.gilding.birthday.client.render.blockentity.TeleportAnchorBlockEntityRenderer;
//import illyena.gilding.birthday.client.render.blockentity.TestBlockEntityRenderer;
import illyena.gilding.birthday.client.render.entity.CapShieldEntityRenderer;
import illyena.gilding.birthday.entity.BirthdayEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static illyena.gilding.birthday.BirthdayInitializer.*;

public class BirthdayRenderers {

    public static void registerRenderers() {
        LOGGER.info("Registering Renderers for " + MOD_NAME + ".");

        EntityRendererRegistry.register(BirthdayEntities.CAP_SHIELD_ENTITY_TYPE, CapShieldEntityRenderer::new);

        BlockEntityRendererRegistry.register(BirthdayBlockEntities.STAR_PORTAL_BLOCK_ENTITY, StarPortalBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(STAR_PORTAL_MODEL_LAYER, StarPortalBlockEntityRenderer.StarPortalModel::getTexturedModelData);

        BlockEntityRendererRegistry.register(BirthdayBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, TeleportAnchorBlockEntityRenderer::new);

    }

    public static final EntityModelLayer STAR_PORTAL_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "star_portal_model"), "main");

}
