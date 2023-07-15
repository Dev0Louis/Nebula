package dev.louis.nebula.mixin;

import dev.louis.nebula.NebulaManager;
import dev.louis.nebula.api.NebulaUser;
import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements NebulaUser {
    private PlayerMixin() {
        super(null, null);
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
    public void setManaManager(ManaManager manaManager) {
        this.manaManager = manaManager;
    }

// Mana End

    
// Knowledge Start

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
// Knowledge End

}
