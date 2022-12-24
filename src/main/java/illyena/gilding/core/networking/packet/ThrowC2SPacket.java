package illyena.gilding.core.networking.packet;

import illyena.gilding.core.item.IThrowable;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ThrowC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        if (player.getMainHandStack().getItem() instanceof IThrowable) {

            throwItem(player);

        }
    }

    public static void throwItem(LivingEntity player) {
        player.setCurrentHand(player.getActiveHand());
        ItemStack stack = player.getActiveItem();

        if (!stack.isEmpty() && stack.getItem() instanceof IThrowable item) {
            item.onThrow(stack, player.world, player, player.getItemUseTimeLeft());
            player.clearActiveItem();
        }
    }

}
