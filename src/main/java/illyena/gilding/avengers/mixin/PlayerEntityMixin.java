package illyena.gilding.avengers.mixin;

import illyena.gilding.avengers.item.custom.CapShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Mixin that allows custom shields to be damaged, and to be disabled with axes.
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "damageShield(F)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void damageShield(float amount, CallbackInfo callBackInfo) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack activeItem = player.getActiveItem();

        if (activeItem.getItem() instanceof CapShieldItem) {
            if (amount >= 3.0F) {
                int i = 1 + MathHelper.floor(amount);
                i = MathHelper.clamp(i, 0, activeItem.getMaxDamage() - activeItem.getDamage() -1 );
                Hand hand = player.getActiveHand();

                activeItem.damage(i, (LivingEntity) player, ((playerEntity) -> player.sendToolBreakStatus(hand)));

            }

        }
    }
}
//todo generify