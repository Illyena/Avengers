package illyena.gilding.birthday.client.render.blockentity;

import com.google.common.collect.ImmutableList;
import illyena.gilding.birthday.block.StarPortalBlock;
import illyena.gilding.birthday.block.blockentity.StarPortalBlockEntity;
import illyena.gilding.birthday.client.render.BirthdayRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

@Environment(EnvType.CLIENT)
public class StarPortalBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<StarPortalBlockEntity> {
    private final StarPortalModel model;

    public StarPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new StarPortalModel(ctx.getLayerModelPart(BirthdayRenderers.STAR_PORTAL_MODEL_LAYER));
    }

    public void render(StarPortalBlockEntity starPortalBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = Direction.UP;
        if (starPortalBlockEntity.hasWorld()) {
            BlockState blockState = starPortalBlockEntity.getWorld().getBlockState(starPortalBlockEntity.getPos());
            if (blockState.getBlock() instanceof StarPortalBlock) {
                direction = (Direction)blockState.get(StarPortalBlock.FACING);
            }
        }

        DyeColor dyeColor = starPortalBlockEntity.getColor();
        SpriteIdentifier spriteIdentifier;
        if (dyeColor == null) {
            spriteIdentifier = TexturedRenderLayers.SHULKER_TEXTURE_ID;
        } else {
            spriteIdentifier = (SpriteIdentifier)TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
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
        this.model.getShellParts().forEach((part) -> {
            part.render(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        });

//        this.model.base.render(matrixStack, vertexConsumer, i, j); //todo forTesting
//        this.model.lid.render(matrixStack, vertexConsumer, i, j);  //todo forTesting

        float g = 0.1f *( MathHelper.sin(starPortalBlockEntity.getPulseProgress(tickDelta) / 6.0f)) + 1.0f;;
        this.model.head.setPivot(1.0f - g, 1.0f - g - starPortalBlockEntity.getAnimationProgress(tickDelta), 1.0f - g );
        matrixStack.translate(0.0f, 0.75f, 0.0f);
        matrixStack.scale(g, g, g);

        this.model.head.render(matrixStack, vertexConsumerProvider.getBuffer(this.getLayer()), i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();



        //end gateway //todo is this needed
//        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
//        this.renderSides(starPortalBlockEntity, matrix4f, vertexConsumerProvider.getBuffer(this.getLayer()));

    }

 /*   private void renderSides(StarPortalBlockEntity entity, Matrix4f matrix, VertexConsumer vertexConsumer) {
        float f = this.getBottomYOffset();
        float g = this.getTopYOffset();
        this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderSide(entity, matrix, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderSide(entity, matrix, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, g, g, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderSide(StarPortalBlockEntity entity, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction side) {
        if (entity.shouldDrawSide(side)) {
            vertices.vertex(model, x1, y1, z1).next();
            vertices.vertex(model, x2, y1, z2).next();
            vertices.vertex(model, x2, y2, z3).next();
            vertices.vertex(model, x1, y2, z4).next();
        }

    }

    protected float getTopYOffset() {
        return 0.75F;
    }

    protected float getBottomYOffset() {
        return 0.375F;
    }
*/
    protected RenderLayer getLayer() {
        return RenderLayer.getEndPortal();
    }



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

} //todo CLEAN
