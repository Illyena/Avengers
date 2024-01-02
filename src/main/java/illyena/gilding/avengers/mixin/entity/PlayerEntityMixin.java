package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof MjolnirItem item && !item.isWorthy(player)) {
            item.notWorthy(stack, player.getWorld(), player);
            ci.cancel();
        }
    }

}
