package illyena.gilding.avengers.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static illyena.gilding.avengers.AvengersInit.*;


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

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f = 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
//        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = 1.0F;
        int l = MathHelper.ceil(255.0F) << 24;
        if ((l & -67108864) != 0) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

            drawTexture(matrices, this.width / 2 - 80 , 10, 0.0f, 0.0f, 160, 80, 160, 160);
            String string = "Minecraft " + SharedConstants.getGameVersion().getName();

            if (MinecraftClient.getModStatus().isModded()) {
                string = string + I18n.translate("menu.modded", Mod.getModsWithSubGroups(MOD_ID).toArray());
            }

            drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);


            super.render(matrices, mouseX, mouseY, delta);


        }
    }
}
