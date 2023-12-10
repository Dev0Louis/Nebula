package dev.louis.nebula.mixin;

import dev.louis.nebula.NebulaManager;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(
        export = true
)
@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements NebulaPlayer {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private ManaManager manaManager;
    @Unique
    private SpellManager spellManager;

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.writeNbt(nbt);
        this.spellManager.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.createManagersIfNecessary();
        this.manaManager.readNbt(nbt);
        this.spellManager.readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaAndSpellManager(CallbackInfo ci) {
        this.manaManager.tick();
        this.spellManager.tick();
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    public void informManaAndSpellManagerOfDeath(DamageSource damageSource, CallbackInfo ci) {
        this.manaManager.onDeath(damageSource);
        this.spellManager.onDeath(damageSource);
    }

    @Override
    public ManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public ManaManager setManaManager(ManaManager manaManager) {
        return this.manaManager = manaManager;
    }

    @Override
    public SpellManager getSpellManager() {
        return this.spellManager;
    }

    @Override
    public SpellManager setSpellManager(SpellManager spellManager) {
        return this.spellManager = spellManager;
    }

    @Override
    public void createManagersIfNecessary() {
        if (this.manaManager == null) this.setManaManager(NebulaManager.createManaManager((PlayerEntity) (Object) this));
        if (this.spellManager == null) this.setSpellManager(NebulaManager.createSpellManager((PlayerEntity) (Object) this));
    }
}
