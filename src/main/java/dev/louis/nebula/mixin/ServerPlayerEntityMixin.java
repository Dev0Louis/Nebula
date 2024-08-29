package dev.louis.nebula.mixin;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellCaster;
import dev.louis.nebula.api.spell.SpellException;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntityMixin implements SpellCaster<ServerPlayerEntity> {
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void castSpell(Spell<ServerPlayerEntity> spell, Transaction transaction) throws SpellException {
        spell.cast(this.nebula$getCaster(), transaction);
    }

    /* These two method need to implemented to remap the interface */
    @Override
    public World getWorld() {
        return super.getWorld();
    }

    @Override
    public Vec3d getPos() {
        return super.getPos();
    }

    @Override
    public ServerPlayerEntity nebula$getCaster() {
        return (ServerPlayerEntity) (Object) this;
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void copyNebulaStuffFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (alive) {
            this.manaManager = ((ServerPlayerEntityMixin) (Object) oldPlayer).manaManager;
        }
    }
}
