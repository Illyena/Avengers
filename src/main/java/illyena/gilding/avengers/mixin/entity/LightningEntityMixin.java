package illyena.gilding.avengers.mixin.entity;

import illyena.gilding.avengers.advancement.AvengersAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(LightningEntity.class)
public class LightningEntityMixin {

    @Shadow @Nullable private ServerPlayerEntity channeler;
    @Shadow @Final private Set<Entity> struckEntities;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/ChanneledLightningCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/Collection;)V"))
    private void onTick(CallbackInfo ci) {
        AvengersAdvancements.KILLED_WITH_CHANNELING.trigger(this.channeler, this.struckEntities.stream().filter(entity -> !entity.isAlive()).toList());

    }
}