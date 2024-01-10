package illyena.gilding.avengers.client.render.blockentity;

import illyena.gilding.avengers.block.blockentity.TeleportAnchorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TeleportAnchorBlockEntityRenderer implements BlockEntityRenderer<TeleportAnchorBlockEntity> {

    public TeleportAnchorBlockEntityRenderer(BlockEntityRendererFactory.Context context) { }

    public void render(TeleportAnchorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.shouldRenderBeam() ) {
            float g = entity.getBeamHeight(tickDelta);
            double d = entity.getWorld().getTopY();
            int k = MathHelper.floor((double) g * d);
            long l = entity.getWorld().getTime();
            float[] color = entity.getColorAssist().getChromaticFadeColorRGB(entity.beamAge, tickDelta);
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, g, l, -k, k * 2, color, 0.15f, 0.175f);
        }
    }

}
