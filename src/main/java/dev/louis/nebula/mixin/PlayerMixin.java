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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements NebulaPlayer {
    @Shadow public abstract boolean shouldDamagePlayer(PlayerEntity player);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

//Mana Start

    public ManaManager manaManager = NebulaManager.createManaManager((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void addManaToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        getManaManager().writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void addManaFromNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        getManaManager().readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManager(CallbackInfo ci) {
        manaManager.tick();
    }
    @Override
    public ManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public ManaManager setManaManager(ManaManager manaManager) {
        return this.manaManager = manaManager;
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeathManaManger(DamageSource damageSource, CallbackInfo ci) {
        this.manaManager.onDeath(damageSource);
    }

// Mana End

    
// Spell Start

    private SpellManager spellManager = NebulaManager.createSpellManager((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void addSpellsToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.spellManager.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void getSpellsFromNbt(NbtCompound nbt, CallbackInfo ci) {
        spellManager.readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickSpellManager(CallbackInfo ci) {
        spellManager.tick();
    }

    @Override
    public SpellManager getSpellManager() {
        return this.spellManager;
    }

    @Override
    public SpellManager setSpellManager(SpellManager spellManager) {
        return this.spellManager = spellManager;
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeathSpellManager(DamageSource damageSource, CallbackInfo ci) {
        this.spellManager.onDeath(damageSource);
    }
// Spell End
}
