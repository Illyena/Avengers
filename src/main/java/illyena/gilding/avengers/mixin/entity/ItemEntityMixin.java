package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.item.custom.MjolnirItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    ItemEntity itemEntity = (ItemEntity)(Object)this;
    @Shadow public abstract ItemStack getStack();

    @Shadow private int pickupDelay;

    @Shadow private int health;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.getStack().getItem() instanceof MjolnirItem item && !this.itemEntity.world.isClient()) {
            if (this.pickupDelay == 0){
                this.itemEntity.world.setBlockState(this.itemEntity.getBlockPos(), item.getBlock().getDefaultState().with(MjolnirBlock.FACING, this.itemEntity.getHorizontalFacing()));
                if (this.itemEntity.world.getBlockEntity(this.itemEntity.getBlockPos()) instanceof MjolnirBlockEntity blockEntity) {
                    blockEntity.setDamage(this.getStack().getDamage());
                }
                this.itemEntity.discard();
            }

            if (this.itemEntity.getY() < this.itemEntity.world.getBottomY()) {
                BlockPos blockPos = new BlockPos(this.itemEntity.getX(), this.itemEntity.world.getBottomY() + 1, this.itemEntity.getZ());
                this.itemEntity.world.setBlockState(blockPos, item.getBlock().getDefaultState().with(MjolnirBlock.FACING, this.itemEntity.getHorizontalFacing()));
                if (this.itemEntity.world.getBlockEntity(blockPos) instanceof MjolnirBlockEntity blockEntity) {
                    blockEntity.setDamage(this.getStack().getDamage());
                }
                this.itemEntity.discard();
            }

        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.getStack().getItem() instanceof MjolnirItem item) {
            this.health = Math.max(1, this.health - 1);
            this.itemEntity.world.setBlockState(this.itemEntity.getBlockPos(), item.getBlock().getDefaultState().with(MjolnirBlock.FACING, this.itemEntity.getHorizontalFacing()));
            if (this.itemEntity.world.getBlockEntity(this.itemEntity.getBlockPos()) instanceof MjolnirBlockEntity blockEntity) {
                blockEntity.setDamage(item.getMaxDamage() - this.health);
            }
            this.itemEntity.discard();
        }
        cir.setReturnValue(true);
        cir.cancel();
    }


}
