package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.item.AvengersItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead & ModelWithArms> {
    @Shadow @Final private HeldItemRenderer heldItemRenderer;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, boolean bl, ItemStack itemStack, ItemStack itemStack2) {
             if (itemStack.isOf(AvengersItems.MJOLNIR)  && itemStack.equals(livingEntity.getActiveItem())) {
            this.renderMjolnirCharging(livingEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i, j);
//            this.renderMjolnirCharging(livingEntity, itemStack, ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i, j);
            matrixStack.pop();
            ci.cancel();
       } else if (itemStack2.isOf(AvengersItems.MJOLNIR)  && itemStack2.equals(livingEntity.getActiveItem())) {
            this.renderMjolnirCharging(livingEntity, itemStack2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i, j);
//            this.renderMjolnirCharging(livingEntity, itemStack2, ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i, j);
            matrixStack.pop();
            ci.cancel();
        }
    }

    private void renderMjolnirCharging(LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float animationProgress) {
        HeldItemFeatureRenderer<?,?> renderer = (HeldItemFeatureRenderer<?,?>) (Object) this;
        if (!stack.isEmpty()) {
            matrices.push();

            renderer.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;

            matrices.translate((bl ? -1.0f : 1.0f) / 16.0F, 0.0f, -0.625);
            float n = animationProgress * -45.0f;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(n));
            matrices.translate(0.0f, MathHelper.sign(n) * -0.45f, 0.0f);

            this.heldItemRenderer.renderItem(entity, stack, mode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }


    }


}
