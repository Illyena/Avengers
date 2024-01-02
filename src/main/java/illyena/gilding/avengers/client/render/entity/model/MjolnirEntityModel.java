package illyena.gilding.avengers.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class MjolnirEntityModel extends Model {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart handle;

    public MjolnirEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.root = root;
        this.head = root.getChild("head");
        this.handle = root.getChild("handle");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv( 12, 22).cuboid(-2.0f, -3.0f, 0.5f,  4.0f, 4.0f, 6.0f, Dilation.NONE), ModelTransform.NONE);
        modelPartData.addChild("handle", ModelPartBuilder.create().uv(10, 15).cuboid(-1.0f, -7.5f, -4.5f, 2.0f, 11.0f, 2.0f, Dilation.NONE), ModelTransform.rotation((float) Math.PI, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public ModelPart getHead() { return this.head; }

    public ModelPart getHandle() { return this.handle; }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

}
