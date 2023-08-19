package dev.louis.nebula.mixin;

import dev.louis.nebula.NebulaManager;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.MultiTickSpell;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements NebulaPlayer {
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
// Spell End

// MultiTickSpell Start
    private Collection<MultiTickSpell> multiTickSpells = new ArrayList<>();
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMultiTickSpells(CallbackInfo ci) {
        multiTickSpells.removeIf(multiTickSpell -> !multiTickSpell.shouldContinue());
        for (MultiTickSpell multiTickSpell : multiTickSpells) {
            multiTickSpell.tick();
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void stopMultiTickSpellsOnDeaths(DamageSource damageSource, CallbackInfo ci) {
        for(MultiTickSpell multiTickSpell : multiTickSpells) {
            multiTickSpell.stop(true);
        }
    }

    public Collection<MultiTickSpell> getMultiTickSpells() {
        return this.multiTickSpells;
    }

    public Collection<MultiTickSpell> setMultiTickSpells(Collection<MultiTickSpell> multiTickSpells) {
        return this.multiTickSpells = multiTickSpells;
    }
// MultiTickSpell End
}
