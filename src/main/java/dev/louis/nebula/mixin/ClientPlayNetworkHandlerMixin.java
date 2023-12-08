package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(
            method = "onPlayerRespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;init()V"
            )
    )
    public void transportManager(
            CallbackInfo ci,
            @Local(ordinal = 0) ClientPlayerEntity oldClientPlayer,
            @Local(ordinal = 1) ClientPlayerEntity newClientPlayer
    ) {
        newClientPlayer.setSpellManager(oldClientPlayer.getSpellManager().setPlayer(newClientPlayer));
        newClientPlayer.setManaManager(oldClientPlayer.getManaManager().setPlayer(newClientPlayer));
    }
}
