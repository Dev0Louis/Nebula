package dev.louis.nebula.mixin;

import dev.louis.nebula.mana.InternalManaManagerHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntityMixin {
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void copyNebulaStuffFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (alive) {
            InternalManaManagerHolder.getManaManager(((ServerPlayerEntity) (Object) this)).copyFrom(InternalManaManagerHolder.getManaManager(oldPlayer));
        }
    }
}
