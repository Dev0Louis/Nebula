package dev.louis.nebula.mixin;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaPoolHolder;
import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder, SpellEffectHolder {
    @Shadow public abstract boolean shouldRenderName();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    @Unique
    protected NebulaManaManager manaManager;

    @Unique
    protected boolean iterating;
    @Unique
    protected Collection<SpellEffect> spellEffects = new ArrayList<>();
    @Unique
    protected Collection<SpellEffect> terminatedSpells = new ArrayList<>(1);


    @Inject(
            method = "<init>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;attributes:Lnet/minecraft/entity/attribute/AttributeContainer;", shift = At.Shift.AFTER)
    )
    public void lateManaManagerInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        manaManager = Nebula.createManaManager((LivingEntity) (Object) this);
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
    public void tickManaManagerAndSpellEffects(CallbackInfo ci) {
        this.manaManager.tick();

        terminatedSpells.stream().peek(spellEffects::remove).forEach(SpellEffect::onEnd);

        iterating = true;
        for (SpellEffect spellEffect : spellEffects) {
            if (!spellEffect.shouldContinue()) {
                endSpellEffect(spellEffect);
                continue;
            }
            spellEffect.tick();
        }
        iterating = false;

    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public @NotNull ManaPool getManaPool() {
        return this.manaManager;
    }

    @Override
    public void startSpellEffect(SpellEffect spellEffect) {

        this.spellEffects.add(spellEffect);
        spellEffect.onEnd();
    }

    @Override
    public void endSpellEffect(SpellEffect spellEffect) {
        if (!iterating){
            this.spellEffects.remove(spellEffect);
        } else {
            this.terminatedSpells.add(spellEffect);
        }
    }
}
