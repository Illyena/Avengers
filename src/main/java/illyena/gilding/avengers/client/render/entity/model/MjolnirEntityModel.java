package illyena.gilding.avengers.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

@Environment(EnvType.CLIENT)
public class MjolnirEntityModel extends Model {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart shaft;

    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mjolnir.png");

    public MjolnirEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.root = root;
        this.head = root.getChild("head");
        this.shaft = root.getChild("shaft");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv( 12, 22).cuboid(-2.0f, -3.0f, 0.5f,  4.0f, 4.0f, 6.0f, Dilation.NONE), ModelTransform.NONE);
        modelPartData.addChild("shaft", ModelPartBuilder.create().uv(10, 15).cuboid(-1.0f, -7.5f, -4.5f, 2.0f, 11.0f, 2.0f, Dilation.NONE), ModelTransform.rotation((float) Math.PI, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
