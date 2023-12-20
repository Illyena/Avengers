package illyena.gilding.avengers.client.render.entity;

import illyena.gilding.avengers.client.render.entity.model.AvengersEntityModelLayers;
import illyena.gilding.avengers.client.render.entity.model.MjolnirEntityModel;
import illyena.gilding.avengers.entity.projectile.MjolnirEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;
import static illyena.gilding.avengers.config.AvengersConfigOptions.MJOLNIR_LEGACY;

@Environment(EnvType.CLIENT)
public class MjolnirEntityRenderer extends EntityRenderer<MjolnirEntity> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mjolnir.png");
    public static final Identifier LEGACY_TEXTURE = new Identifier(MOD_ID, "textures/entity/mjolnir_legacy.png");
    private final MjolnirEntityModel model;

    public MjolnirEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new MjolnirEntityModel(context.getPart(AvengersEntityModelLayers.MJOLNIR_MODEL_LAYER));
    }

    public void render(MjolnirEntity mjolnirEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, mjolnirEntity.prevYaw, mjolnirEntity.getYaw()) - 90.0F));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, mjolnirEntity.prevPitch, mjolnirEntity.getPitch()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(mjolnirEntity)), false, mjolnirEntity.isEnchanted());
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(mjolnirEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(MjolnirEntity mjolnirEntity) { return MJOLNIR_LEGACY.getValue() ? LEGACY_TEXTURE : TEXTURE; }

    public MjolnirEntityModel getModel() { return model; }
}
