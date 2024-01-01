package illyena.gilding.avengers.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static illyena.gilding.avengers.AvengersInit.*;

@Environment(EnvType.CLIENT)
public class AvengersConfigMenu extends ConfigScreen {
    private static final Text TITLE = translationKeyOf("menu", "config.title");
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier(MOD_ID, "textures/gui/title/background/panorama"));
    private static final Identifier TITLE_TEXTURE = new Identifier(MOD_ID, "textures/gui/title/avengers.png");

    public AvengersConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public AvengersConfigMenu(Screen parent) {
        super(MOD_ID, parent, TITLE);
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.doBackgroundFade = true;
    }

    protected void renderText(DrawContext context, int mouseX, int mouseY, float delta, float alpha, int time) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        context.drawTexture(TITLE_TEXTURE, this.width / 2 - 80, 10, 0.0f, 0.0f, 160, 60, 160, 60);

        String string = MOD_NAME + ": " + Mod.getModVersion(MOD_ID);
        int width = this.textRenderer.getWidth(string);
        context.drawTextWithShadow(this.textRenderer, string, this.width - width - 2, this.height - 10, 16777215 | time);
    }

    @Override
    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
