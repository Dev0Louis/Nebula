package dev.louis.nebula.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerMixin {

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void copyNebulaStuffFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (alive) {
            this.manaManager = ((ServerPlayerEntityMixin) (Object) oldPlayer).manaManager;
        }
    }
}
