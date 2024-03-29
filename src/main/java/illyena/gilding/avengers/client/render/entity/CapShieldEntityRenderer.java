package illyena.gilding.avengers.client.render.entity;

import illyena.gilding.avengers.client.render.entity.model.CapShieldEntityModel;
import illyena.gilding.avengers.client.render.entity.model.AvengersEntityModelLayers;
import illyena.gilding.avengers.entity.projectile.CapShieldEntity;
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

@Environment(EnvType.CLIENT)
public class CapShieldEntityRenderer extends EntityRenderer<CapShieldEntity> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/cap_shield.png");
    private final CapShieldEntityModel model;

    public CapShieldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new CapShieldEntityModel(context.getPart(AvengersEntityModelLayers.CAP_SHIELD_MODEL_LAYER));
    }

    public void render(CapShieldEntity capShieldEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, capShieldEntity.prevYaw, capShieldEntity.getYaw()) - 90.0F));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, capShieldEntity.prevPitch, capShieldEntity.getPitch()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(capShieldEntity)), false, capShieldEntity.isEnchanted());
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(capShieldEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(CapShieldEntity capShieldEntity) {
        return TEXTURE;
    }
}
