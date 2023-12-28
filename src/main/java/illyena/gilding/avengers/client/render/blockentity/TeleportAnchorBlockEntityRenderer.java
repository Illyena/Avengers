package illyena.gilding.avengers.client.render.blockentity;

import illyena.gilding.avengers.block.blockentity.TeleportAnchorBlockEntity;
import illyena.gilding.core.util.time.GildingCalendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class TeleportAnchorBlockEntityRenderer implements BlockEntityRenderer<TeleportAnchorBlockEntity> {
    private BeamColorStage beamColors;
    private float prevColorFactor;

    public TeleportAnchorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.beamColors = BeamColorStage.stage_1;
    }

    public void render(TeleportAnchorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.isRecentlyGenerated()) {
            float g = entity.getRecentlyGeneratedBeamHeight(tickDelta);
            double d = entity.getWorld().getTopY();
            g = MathHelper.sin(g * 3.1415927F);
            int k = MathHelper.floor((double) g * d);
            long l = entity.getWorld().getTime();
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, g, l, -k, k * 2, getBeamColor(entity, tickDelta), 0.15F, 0.175F);
        }
    }

    private float[] getBeamColor(TeleportAnchorBlockEntity entity, float tickDelta) {
        switch (GildingCalendar.checkHolidays()) {
            case NEW_YEARS -> { return getBicolorBeamColor(entity, tickDelta); }
            case CHRISTMAS -> { return getTricolorBeamColor(entity, tickDelta); }
            case BIRTHDAY -> { return getJebBeamColor(entity, tickDelta); }
            default -> { return getBicolorBeamColor(entity, tickDelta); }
        }
    }

    private float[] getRgbColor(int i) {
        Map<Integer, float[]> colors;
        switch (GildingCalendar.checkHolidays()) {
            case NEW_YEARS ->  colors = Map.of(1, DyeColor.ORANGE.getColorComponents(), 2, DyeColor.BLACK.getColorComponents());
            case CHRISTMAS ->  colors = Map.of(1, DyeColor.RED.getColorComponents(), 2, DyeColor.GREEN.getColorComponents());
            case BIRTHDAY ->  colors = Map.of(1, DyeColor.ORANGE.getColorComponents(), 2, DyeColor.LIME.getColorComponents());
            default ->  colors = Map.of(1, DyeColor.MAGENTA.getColorComponents(), 2, DyeColor.PURPLE.getColorComponents());
        }
        return colors.get(i);
    }

    private float[] getBicolorBeamColor(TeleportAnchorBlockEntity entity, float tickDelta) {
        float f = entity.getBeamTime();
        float colorFactor = ((entity.age % f) + tickDelta) / f;
        float[] first = getRgbColor(1);
        float[] second = getRgbColor(2);
        float[] beamColor = first;

        if (this.prevColorFactor > colorFactor) {
            this.beamColors = BeamColorStage.next(this.beamColors);
        }
        switch (beamColors) {
            case stage_1 -> {
                float red = first[0] * (1.0F - colorFactor) + second[0] * (colorFactor);
                float green = first[1] * (1.0F - colorFactor) + second[1] * (colorFactor);
                float blue = first[2] * (1.0F - colorFactor) + second[2] * (colorFactor);
                beamColor = new float[]{red, green, blue};
            }
            case stage_2 -> {
                float red = second[0] * (1.0F - colorFactor) + first[0] * colorFactor;
                float green = second[1] * (1.0F - colorFactor) + first[1] * colorFactor;
                float blue = second[2] * (1.0F - colorFactor) + first[2] * colorFactor;
                beamColor = new float[]{red, green, blue};
            }
            case stage_3 -> this.beamColors = BeamColorStage.stage_1;
        }
        this.prevColorFactor = colorFactor;
        return beamColor;
    }

    private float[] getTricolorBeamColor(TeleportAnchorBlockEntity entity, float tickDelta) {
        float f = entity.getBeamTime() / 2;
        float colorFactor = ((entity.age % f) + tickDelta) / f;
        float[] first = getRgbColor(1);
        float[] second = getRgbColor(2);
        float[] beamColor = DyeColor.BLACK.getColorComponents();

        if (prevColorFactor > colorFactor) {
            this.beamColors = BeamColorStage.next(this.beamColors);
        }

        switch (beamColors) {
            case stage_1 -> {
                float red = first[0] - (first[0] * colorFactor);
                float green = first[1] - (first[1] * colorFactor);
                float blue = first[2] - (first[2] * colorFactor);
                beamColor = new float[] {red, green, blue};
            }
            case stage_2 -> {
                float red = second[0] * colorFactor;
                float green = second[1] * colorFactor;
                float blue = second[2] * colorFactor;
                beamColor = new float[]{red, green, blue};
            }
            case stage_3 -> {
                float red = second[0] - (second[0] * colorFactor);
                float green = second[1] - (second[1] * colorFactor);
                float blue = second[2] - (second[2] * colorFactor);
                beamColor = new float[] {red, green, blue};
            }
            case stage_4 -> {
                float red = first[0] * colorFactor;
                float green = first[1] * colorFactor;
                float blue = first[2] * colorFactor;
                beamColor = new float[]{red, green, blue};
            }
        }
        this.prevColorFactor = colorFactor;
        return beamColor;
    }

    private float[] getJebBeamColor(TeleportAnchorBlockEntity entity, float tickDelta) {
        float f = 3.25f;
        float colorFactor = ((entity.age % f) + tickDelta) / f;
        int i = (int) (entity.age / f);

        int o = DyeColor.values().length;
        int p = i % o;
        int q = (i + 1) % o;
        float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
        float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
        float red =   fs[0] * (1.0F - colorFactor) + gs[0] * colorFactor;
        float green = fs[1] * (1.0F - colorFactor) + gs[1] * colorFactor;
        float blue =  fs[2] * (1.0F - colorFactor) + gs[2] * colorFactor;

        return new float[]{red, green, blue};
    }

    private enum BeamColorStage {
        stage_1,
        stage_2,
        stage_3,
        stage_4;

        BeamColorStage() {}

        public static BeamColorStage next(BeamColorStage stage) {
            return switch (stage) {
                case stage_1 -> stage_2;
                case stage_2 -> stage_3;
                case stage_3 -> stage_4;
                case stage_4 -> stage_1;
            };
        }

    }

}
