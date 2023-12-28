package illyena.gilding.avengers.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;

@Environment(EnvType.CLIENT)
public class CapShieldEntityModel extends Model {
    private final ModelPart root;
    private final ModelPart shield;
    private final ModelPart straps;
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/cap_shield.png");

    public CapShieldEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
        this.shield = root.getChild("shield");
        this.straps = root.getChild("straps");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        //shield
        modelPartData.addChild("shield", ModelPartBuilder.create()
                .uv( 0, 43).cuboid(- 4.0F, - 1.0F, 0.0F,  8.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 41).cuboid(- 7.0F, - 2.0F, 0.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 39).cuboid(- 9.0F, - 3.0F, 0.0F, 18.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 37).cuboid(-10.0F, - 4.0F, 0.0F, 20.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 33).cuboid(-11.0F, - 5.0F, 0.0F, 22.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 29).cuboid(-12.0F, - 6.0F, 0.0F, 24.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 25).cuboid(-13.0F, - 7.0F, 0.0F, 26.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 20).cuboid(-14.0F, - 9.0F, 0.0F, 28.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 13).cuboid(-15.0F, -12.0F, 0.0F, 30.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv( 0,  0).cuboid(-16.0F, -20.0F, 0.0F, 32.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv( 0,  9).cuboid(-15.0F, -23.0F, 0.0F, 30.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 17).cuboid(-14.0F, -25.0F, 0.0F, 28.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 23).cuboid(-13.0F, -26.0F, 0.0F, 26.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 27).cuboid(-12.0F, -27.0F, 0.0F, 24.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 31).cuboid(-11.0F, -28.0F, 0.0F, 22.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 35).cuboid(-10.0F, -29.0F, 0.0F, 20.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 39).cuboid(- 9.0F, -30.0F, 0.0F, 18.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv( 0, 41).cuboid(- 7.0F, -31.0F, 0.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        //straps
        modelPartData.addChild("straps", ModelPartBuilder.create()
                .uv(18, 43).cuboid(0.0f,  -13.0f, -5.0f, 2.0f, 10.0f, 1.0f, new Dilation(0.0f))
                .uv(24, 43).cuboid( 0.0F, - 4.0F, -7.0F, 2.0F,  1.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 43).cuboid( 0.0F, -13.0F, -7.0F, 2.0F,  1.0F, 2.0F, new Dilation(0.0F))
                .uv(18, 43).cuboid(14.0F, -13.0F, -5.0F, 2.0F, 10.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 43).cuboid(14.0F, - 4.0F, -7.0F, 2.0F,  1.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 43).cuboid(14.0F, -13.0F, -7.0F, 2.0F,  1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 16.0F, 8.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    public ModelPart getPlate() {
        return this.shield;
    }

    public ModelPart getHandle() {
        return this.straps;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1.0f);
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

}
