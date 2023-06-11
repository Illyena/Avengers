package illyena.gilding.avengers.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static illyena.gilding.avengers.AvengersInit.MOD_ID;


@Environment(EnvType.CLIENT)
public class AvengersConfigMenu extends ConfigScreen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier(MOD_ID, "textures/gui/title/background/panorama"));//todo
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");//todo
    private static final Identifier TITLE_TEXTURE = new Identifier(MOD_ID, "textures/gui/title/avengers.png");
    private final RotatingCubeMapRenderer backgroundRenderer;

    public AvengersConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public AvengersConfigMenu(Screen parent) {
        super(MOD_ID, parent);
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    }

//    protected boolean inactivateButton() { return this.client != null && this.client.world != null; }
/*
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f = 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(1.0f, 0.0F, 1.0F));
        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f );

        drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        drawCenteredText(matrices, this.textRenderer, new LiteralText(MOD_NAME), this.width / 2, this.height / 6, Color.RED.getRGB());

        super.render(matrices, mouseX, mouseY, delta);

    }

 */

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {


        float f = 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
//        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = 1.0F;
        int l = MathHelper.ceil(255.0F) << 24;
        if ((l & -67108864) != 0) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

            context.drawTexture(TITLE_TEXTURE, this.width / 2 - 80 , 10, 0.0f, 0.0f, 160, 80, 160, 160);
/*
            this.drawWithOutline(j, 2, (x, y) -> {
                        this.drawTexture(matrices, x + 0, y, 0, 0, 128, 128);
//                        this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                    });
/*
            if (this.isMinecraft) {
                this.drawWithOutline(j, 30, (x, y) -> {
                    this.drawTexture(matrices, x + 0, y, 0, 0, 99, 44);
                    this.drawTexture(matrices, x + 99, y, 129, 0, 27, 44);
                    this.drawTexture(matrices, x + 99 + 26, y, 126, 0, 3, 44);
                    this.drawTexture(matrices, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                    this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                });
            } else {
                this.drawWithOutline(j, 30, (x, y) -> {
                    this.drawTexture(matrices, x + 0, y, 0, 0, 155, 44);
                    this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                });
            }
*/
//            RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
//            drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 1464, 546);

            String string = "Minecraft " + SharedConstants.getGameVersion().getName();

            if (MinecraftClient.getModStatus().isModded()) {
                string = string + I18n.translate("menu.modded", new Object[0]);
            }

            context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | l);


            super.render(context, mouseX, mouseY, delta);


        }
    }
}
