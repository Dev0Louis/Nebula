package dev.louis.nebula.mixin;

import dev.louis.nebula.networking.SynchronizeSpellsS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerJoinSendSyncSpellKnowledge(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PacketByteBuf buf = PacketByteBufs.create();
        SynchronizeSpellsS2CPacket.create(player).write(buf);
        ServerPlayNetworking.send(player, SynchronizeSpellsS2CPacket.getID(), buf);
    }
}
