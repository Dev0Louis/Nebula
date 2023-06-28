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
    public void copySpellAndManaManagerFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        NebulaPlayer player = NebulaPlayer.access(this);
        player.getSpellManager().copyFrom(oldPlayer, alive);
        player.getManaManager().copyFrom(oldPlayer, alive);

    }

    @Inject(method = "onSpawn", at = @At("RETURN"))
    public void syncManaAndSpellsOnSpawn(CallbackInfo ci) {
        var buf = PacketByteBufs.create();
        UpdateSpellCastabilityS2CPacket.create(this).write(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, UpdateSpellCastabilityS2CPacket.getID(), buf);
        NebulaPlayer.access(this).getManaManager().sendSync();
    }
}
