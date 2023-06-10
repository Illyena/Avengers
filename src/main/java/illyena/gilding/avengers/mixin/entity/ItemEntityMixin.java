package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    private void onTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        if (this.getStack().getItem() instanceof MjolnirItem item && this.pickupDelay == 0 && !itemEntity.world.isClient()) {
            itemEntity.world.setBlockState(itemEntity.getBlockPos(), item.getBlock().getDefaultState().with(MjolnirBlock.FACING, itemEntity.getHorizontalFacing()));
            if (itemEntity.world.getBlockEntity(itemEntity.getBlockPos()) instanceof MjolnirBlockEntity blockEntity) {
                blockEntity.setDamage(this.getStack().getDamage());
            }
            itemEntity.discard();
        }
    }
}
