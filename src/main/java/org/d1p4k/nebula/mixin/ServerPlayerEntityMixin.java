package org.d1p4k.nebula.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.packet.s2c.SpellRegistryS2CPacket;
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
            ((NebulaPlayer) currentPlayer).getSpellKnowledge().copyFrom(oldPlayer);
        }

    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void mixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(alive) {
            ((NebulaPlayer) currentPlayer)
                    .setManaManager(
                            ((NebulaPlayer) oldPlayer).getManaManager().copy((PlayerEntity) (Object) this)
                    );
        }
    }


    @Inject(method = "onSpawn", at = @At("RETURN"))
    public void onDeathMixin(CallbackInfo ci) {
        SpellRegistryS2CPacket.send((ServerPlayerEntity) (Object) this);
    }

}
