package illyena.gilding.core.networking;

import illyena.gilding.core.networking.packet.ThrowC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class GildingPackets {
    public static final Identifier THROW_ID = new Identifier(SUPER_MOD_ID, "throw");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(THROW_ID, ThrowC2SPacket::receive);
    }

    public static void registerS2CPackets() {

    }
}
