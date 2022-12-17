package illyena.gilding.core.event;

import illyena.gilding.core.networking.GildingPackets;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_GILDING = "key.category.gilding.gilding";
    public static final String KEY_TROW = "key.gilding.throw";

    public static KeyBinding throwing;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (throwing.wasPressed()) {
                ClientPlayNetworking.send(GildingPackets.THROW_ID, PacketByteBufs.create());
            }
        });
    }

    public static void register() {
        throwing = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TROW, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_GILDING
        ));
        registerKeyInputs();
    }
}
