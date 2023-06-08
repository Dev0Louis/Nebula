package dev.louis.nebula.mixin;

import com.mojang.authlib.GameProfile;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void spellManagerCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        NebulaPlayer.access(this).getSpellManager().copyFrom(oldPlayer, alive);
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void manaManagerCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        NebulaPlayer.access(this).getManaManager().copyFrom(oldPlayer, alive);
    }

    @Inject(method = "onSpawn", at = @At("RETURN"))
    public void onSpawnSyncSpells(CallbackInfo ci) {
        var buf = PacketByteBufs.create();
        UpdateSpellCastabilityS2CPacket.create(this).write(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, UpdateSpellCastabilityS2CPacket.getID(), buf);

    }
}
