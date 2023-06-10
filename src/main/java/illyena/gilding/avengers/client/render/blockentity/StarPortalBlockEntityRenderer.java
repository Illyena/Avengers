package illyena.gilding.avengers.client.render.blockentity;

import com.google.common.collect.ImmutableList;
import illyena.gilding.avengers.block.StarPortalBlock;
import illyena.gilding.avengers.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.avengers.client.render.AvengersRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;

@Environment(EnvType.CLIENT)
public class StarPortalBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<StarPortalBlockEntity> {
    private final StarPortalModel model;

    public StarPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new StarPortalModel(ctx.getLayerModelPart(AvengersRenderers.STAR_PORTAL_MODEL_LAYER));
    }

    public void render(StarPortalBlockEntity starPortalBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = Direction.UP;
        if (starPortalBlockEntity.hasWorld()) {
            BlockState blockState = starPortalBlockEntity.getWorld().getBlockState(starPortalBlockEntity.getPos());
            if (blockState.getBlock() instanceof StarPortalBlock) {
                direction = blockState.get(StarPortalBlock.FACING);
            }
        }

        DyeColor dyeColor = starPortalBlockEntity.getColor();
        SpriteIdentifier spriteIdentifier;
        if (dyeColor == null) {
            spriteIdentifier = TexturedRenderLayers.SHULKER_TEXTURE_ID;
        } else {
            spriteIdentifier = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
        }

        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.9995f, 0.9995f, 0.9995f);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(0.0, -1.0, 0.0);

        this.model.lid.setPivot(0.0F, 24.0F - starPortalBlockEntity.getAnimationProgress(tickDelta) * 0.5F * 16.0F, 0.0F);
        this.model.lid.yaw = 270.0F * starPortalBlockEntity.getAnimationProgress(tickDelta) * 0.017453292F;



        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.model.getShellParts().forEach(part -> part.render(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f));

        float g = 0.1f *( MathHelper.sin(starPortalBlockEntity.getPulseProgress(tickDelta) / 6.0f)) + 1.0f;
        this.model.head.setPivot(1.0f - g, 1.0f - g - starPortalBlockEntity.getAnimationProgress(tickDelta), 1.0f - g );
        matrixStack.translate(0.0f, 0.75f, 0.0f);
        matrixStack.scale(g, g, g);

        this.model.head.render(matrixStack, vertexConsumerProvider.getBuffer(this.getLayer()), i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();

    }

    protected RenderLayer getLayer() { return RenderLayer.getEndPortal(); }


    @Environment(EnvType.CLIENT)
    public static final class StarPortalModel extends Model {
        private final ModelPart root;
        private final ModelPart base;
        private final ModelPart lid;
        private final ModelPart head;

        public StarPortalModel(ModelPart root) {
            super(RenderLayer::getEntityCutoutNoCullZOffset);
            this.root = root;
            this.lid = this.root.getChild("lid");
            this.base = this.root.getChild("base");
            this.head = this.root.getChild("head");
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();

            modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 28).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 52).cuboid(-5.0F, -2.0F, -5F, 10.0F, 10.0F, 10.0F), ModelTransform.pivot(0.0F, 12.0F, 0.0F));

            return TexturedModelData.of(modelData, 64, 64);
        }

        public ModelPart getParts() { return this.root; }

        public Iterable<ModelPart> getShellParts() {
            return ImmutableList.of(this.root.getChild("base"), this.root.getChild("lid"));
        }

        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

}
