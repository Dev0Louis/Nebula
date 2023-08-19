package dev.louis.nebula.mixin;

import com.mojang.authlib.GameProfile;
import dev.louis.nebula.Nebula;
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
    public void copyNebulaStuffFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.getSpellManager().copyFrom(oldPlayer, alive);
        this.getManaManager().copyFrom(oldPlayer, alive);
        if(alive) {
            this.setMultiTickSpells(oldPlayer.getMultiTickSpells());
        }
    }

    @Inject(method = "onSpawn", at = @At("RETURN"))
    public void syncManaAndSpellsOnSpawn(CallbackInfo ci) {
        boolean haveSpellsSynced = this.getSpellManager().sendSync();
        if(!haveSpellsSynced) Nebula.LOGGER.info("Spells could not be synced!");

        boolean hasManaSynced = this.getManaManager().sendSync();
        if(!hasManaSynced) Nebula.LOGGER.info("Mana could not be synced!");
    }
}
