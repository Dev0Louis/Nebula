package org.d1p4k.nebula.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    ServerPlayerEntity currentPlayer = (ServerPlayerEntity) (Object) this;


    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void KnowledgeMixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(alive) {
            ((NebulaPlayer) currentPlayer).setCastableSpells(((NebulaPlayer) oldPlayer).getCastableSpells());
        }

    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void mixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ((NebulaPlayer) currentPlayer)
                .setManaManger(
                        ((NebulaPlayer) oldPlayer).getManaManger()
                );
    }

}
