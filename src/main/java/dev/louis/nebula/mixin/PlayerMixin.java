package dev.louis.nebula.mixin;

import dev.louis.nebula.InternalNebulaPlayer;
import dev.louis.nebula.api.mana.ManaHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellCaster;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin implements InternalNebulaPlayer, ManaHolder, SpellCaster<PlayerEntity> {

    @Unique
    protected NebulaManaManager manaManager = new NebulaManaManager((PlayerEntity) (Object) this);

    @Override
    public void castSpell(Spell<PlayerEntity> spell) {

    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaAndSpellManager(CallbackInfo ci) {
        this.manaManager.tick();
    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }
}
