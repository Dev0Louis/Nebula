package dev.louis.nebula.mixin;

import dev.louis.nebula.api.mana.manager.ManaManagerHolder;
import dev.louis.nebula.networking.s2c.play.ManaPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntityMixin {

    @Unique
    private long lastSyncedThaum = -1;
    @Unique
    private long lastSyncedCapacity = -1;

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    public void checkManaSync(CallbackInfo ci) {
        var thaum = this.getManaManager().getThaum();
        var capacity = this.getManaManager().getThaumCapacity();

        if (thaum != lastSyncedThaum || capacity != lastSyncedCapacity) {
            var srvrPlyr = ((ServerPlayerEntity) (Object) this);
            ServerPlayNetworking.send(srvrPlyr, new ManaPayload(srvrPlyr.getId(), thaum, capacity));
            this.lastSyncedCapacity = capacity;
            this.lastSyncedThaum = thaum;
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void copyNebulaStuffFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (alive) {
            ManaManagerHolder.getManaManager(((ServerPlayerEntity) (Object) this)).copyFrom(ManaManagerHolder.getManaManager(oldPlayer));
        }
    }
}
