package illyena.gilding.avengers.client.render;
/*
import illyena.gilding.avengers.client.render.entity.model.AvengersEntityModelLayers;
import illyena.gilding.avengers.item.AvengersItems;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

public class MjolnirChargingFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T> & ModelWithHead & ModelWithArms> extends HeldItemFeatureRenderer<T, M> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mjolnir.png");
    private final HeldItemRenderer playerHeldItemRenderer;
    private ModelPart part;

    public MjolnirChargingFeatureRenderer(FeatureRendererContext<T, M> context, EntityRendererFactory.Context ctx) {
        super(context, ctx.getHeldItemRenderer());
        this.playerHeldItemRenderer = ctx.getHeldItemRenderer();
        this.part = ctx.getPart(AvengersRenderers.MJOLNIR_CHARGING_MODEL_LAYER);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
//        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0f, 0.0f, -6.0f, 6.0f, 5.0f, 10.0f), ModelTransform.rotation(1.5f, 0.0f, 0.0f));
        return  TexturedModelData.of(modelData, 16, 16);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack stack = livingEntity.getActiveItem();
        if (stack.isOf(AvengersItems.MJOLNIR)) {
            boolean rightHanded = livingEntity.getMainArm() == Arm.RIGHT;
            boolean mainHand = stack.equals(livingEntity.getMainHandStack());
            ModelTransformation.Mode mode = mainHand ?
                    (rightHanded ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) :
                    (rightHanded ? ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND : ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
            boolean bl = mainHand == (livingEntity.getMainArm() == Arm.LEFT);
            matrices.push();
            if (this.getContextModel().child) {
                matrices.translate(0.0f, 0.75f, 0.0f);
                matrices.scale(0.5f, 0.5f, 0.5f);
            }

            matrices.scale(2.0f, 2.0f, 2.0f);

            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
//            boolean bl = arm == Arm.LEFT;

            matrices.translate((bl ? -1.0f : 1.0f) / 16.0F, 0.0f, -0.625);
            float n = animationProgress * -45.0f;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(n));
            matrices.translate(0.0f, MathHelper.sign(n) * -0.45f, 0.0f);

            this.playerHeldItemRenderer.renderItem(livingEntity, stack, mode, bl, matrices, vertexConsumerProvider, light);
            matrices.pop();
        }

    }

    /*
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
        ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
        if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
            matrices.push();
            if (this.getContextModel().child) {
                float m = 0.5F;
                matrices.translate(0.0, 0.75, 0.0);
                matrices.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderItem(livingEntity, itemStack2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrices, vertexConsumerProvider, light, animationProgress);
            this.renderItem(livingEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrices, vertexConsumerProvider, light, animationProgress);
            matrices.pop();
        }
    }

     */
/*
    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformation, Arm arm, MatrixStack matrices, VertexConsumerProvider consumers, int light, float animationProgress) {
        if(entity.getActiveItem().isOf(AvengersItems.MJOLNIR) && stack == entity.getActiveItem()) {
            this.renderMjolnirCharging(entity, stack, transformation, arm, matrices, consumers, light, animationProgress);
        } else {
            super.renderItem(entity, stack, transformation, arm, matrices, consumers, light);
        }
    }

    private void renderMjolnirCharging(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformation, Arm arm, MatrixStack matrices, VertexConsumerProvider consumers, int light, float animationProgress) {
        if (!stack.isEmpty()) {
            matrices.push();

//            renderer.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;

            matrices.translate((bl ? -1.0f : 1.0f) / 16.0F, 0.0f, -0.625);
            float n = animationProgress * -45.0f;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(n));
            matrices.translate(0.0f, MathHelper.sign(n) * -0.45f, 0.0f);

            this.playerHeldItemRenderer.renderItem(entity, stack, transformation, bl, matrices, consumers, light);
            matrices.pop();
        }

        /*
        for (int m = 0; m < 3; ++m) {
            matrices.push();

            this.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;
//            matrices.translate((bl ? -1.0f : 1.0f) / 16.0F, 0.125, -0.625);

            matrices.translate((bl ? -1.0f : 1.0f) / 16.0f, 1.25f, -0.625f);
            float n = animationProgress * (float)(-(45 + m * 5));
//            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(n));

            this.playerHeldItemRenderer.renderItem(entity, stack, transformation, bl, matrices, consumers, light);
            matrices.pop();

        }

 */
/*
    }


}

/*
public class MjolnirChargingFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mjolnir.png");
    private ModelPart part;

    public MjolnirChargingFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
      super(context);
      ModelPart modelPart = loader.getModelPart(AvengersRenderers.MJOLNIR_CHARGING_MODEL_LAYER);
      this.part = modelPart;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0f, 0.0f, -6.0f, 6.0f, 5.0f, 10.0f), ModelTransform.rotation(1.5f, 0.0f, 0.0f));
        return  TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getActiveItem().isOf(AvengersItems.MJOLNIR)) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(new Identifier("textures/block/acacia_log.png")));

 //           for(int m = 0; m < 3; ++m) {
                matrices.push();
 //               float n = animationProgress * (float)(-(45 + m * 5));
  //              matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(n));
//                float o = 0.75F * (float)m;
//                matrices.scale(o, o, o);
 //               matrices.translate(0.0, (double)(-0.2F + 0.6F * (float)m), 0.0);
                this.part.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
 //           }

        }
    }
}
*/