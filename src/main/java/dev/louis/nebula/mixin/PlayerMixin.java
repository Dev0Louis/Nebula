package dev.louis.nebula.mixin;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.knowledge.SpellKnowledgeManager;
import dev.louis.nebula.manamanager.player.PlayerManaManager;
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
public abstract class PlayerMixin extends LivingEntity implements NebulaPlayer {
    private PlayerMixin() {
        super(null, null);
    }

    //Mana Start

    public PlayerManaManager playerManaManager = Nebula.INSTANCE.createManaManager((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void addManaToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        getPlayerManaManager().writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void addManaFromNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        getPlayerManaManager().readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManager(CallbackInfo ci) {
        playerManaManager.tick();
    }

    @Override
    public int getMana() {
        return playerManaManager.getMana();
    }

    @Override
    public void setMana(int mana) {
        this.playerManaManager.setMana(mana);
    }

    @Override
    public PlayerManaManager getPlayerManaManager() {
        return this.playerManaManager;
    }

    @Override
    public void setPlayerManaManager(PlayerManaManager manaManager) {
        this.playerManaManager = manaManager;
    }

// Mana End

    
// Knowledge Start

    private final SpellKnowledgeManager spellKnowledge = new SpellKnowledgeManager((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void addKnowledgeToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        this.spellKnowledge.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void addKnowledgeFromNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        spellKnowledge.readNbt(nbt);
    }
    @Override
    public SpellKnowledgeManager getSpellKnowledge() {
        return this.spellKnowledge;
    }

// Knowledge End

}
