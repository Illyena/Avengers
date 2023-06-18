package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getStack();

    @Shadow private int pickupDelay;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick2(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        if (this.getStack().getItem() instanceof MjolnirItem item && !itemEntity.world.isClient) {
            if (this.pickupDelay == 0) {
                itemEntity.world.setBlockState(itemEntity.getBlockPos(), item.getBlock().getDefaultState());
                itemEntity.discard();
            }
        }
        if (itemEntity.getBlockPos().getY() <= itemEntity.world.getBottomY() && itemEntity.getStack().getItem() instanceof MjolnirItem item) {
            BlockPos blockPos = itemEntity.getBlockPos().withY(itemEntity.world.getBottomY() + 1);
            itemEntity.world.setBlockState(blockPos, item.getBlock().getDefaultState());
        }
    }
}
