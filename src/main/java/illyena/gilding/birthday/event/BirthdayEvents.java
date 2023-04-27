package illyena.gilding.birthday.event;

import illyena.gilding.birthday.block.blockentity.TeleportAnchorBlockEntity;
import illyena.gilding.core.event.TeleportCallback;
import net.minecraft.util.ActionResult;

public class BirthdayEvents {
    public static void registerEvents() {
        TeleportCallback.TELEPORT_EVENT.register((world, player, pos) -> {
            if (world.getBlockEntity(pos) instanceof TeleportAnchorBlockEntity blockEntity) {
                TeleportAnchorBlockEntity.startTeleportCooldown(world, pos, world.getBlockState(pos), blockEntity);

                return ActionResult.FAIL;
            } else {
                return ActionResult.PASS;
            }
        });
    }
}
