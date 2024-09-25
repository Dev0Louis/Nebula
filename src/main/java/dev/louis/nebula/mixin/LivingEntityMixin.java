package dev.louis.nebula.mixin;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaPoolHolder;
import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"AddedMixinMembersNamePattern", "UnreachableCode"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder, SpellEffectHolder {

    private static final String SPELL_EFFECTS = "SpellEffects";
    private static final String SPELL_EFFECT_ID = "id";
    private static final String SPELL_EFFECT_DATA = "data";

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
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);

        this.manaManager.writeNbt(nebulaNbt);

        NbtList spellEffectsNbt = new NbtList();
        for (SpellEffect spellEffect : spellEffects) {
            var spellEffectNbt = new NbtCompound();
            spellEffectNbt.putString(SPELL_EFFECT_ID, SpellEffectType.REGISTRY.getId(spellEffect.getType()).toString());
            spellEffectNbt.put(SPELL_EFFECT_DATA, spellEffect.writeNbt(new NbtCompound()));
            spellEffectsNbt.add(spellEffectNbt);
        }
        nebulaNbt.put(SPELL_EFFECTS, spellEffectsNbt);

        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.manaManager.readNbt(nebulaNbt);

        var spellEffects = new ArrayList<SpellEffect>();

        for (NbtElement nbtElement : nebulaNbt.getList(SPELL_EFFECTS, NbtCompound.END_TYPE)) {
            var spellEffectNbt = (NbtCompound) nbtElement;
            var id = Identifier.tryParse(spellEffectNbt.getString(SPELL_EFFECT_ID));
            var spellEffectType = SpellEffectType.REGISTRY.get(id);

            if (spellEffectType == null) {
                Nebula.LOGGER.warn("Spell effect " + id + " wasn't registered! This can happen if you remove Mods!");
                continue;
            }

            var spellEffect = spellEffectType.factory().create((LivingEntity) (Object) this);
            spellEffect.readNbt(spellEffectNbt.getCompound(SPELL_EFFECT_DATA));
            spellEffects.add(spellEffect);
        }

        this.spellEffects = spellEffects;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManagerAndSpellEffects(CallbackInfo ci) {
        this.manaManager.tick();

        terminatedSpells.stream().peek(spellEffects::remove).forEach(SpellEffect::onEnd);
        terminatedSpells.clear();

        iterating = true;
        for (SpellEffect spellEffect : spellEffects) {
            spellEffect.age++;
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
    public boolean startSpellEffect(SpellEffect spellEffect) {
        if (spellEffect.canStart(this.spellEffects) && this.spellEffects.add(spellEffect)) {
            spellEffect.onStart();
            return true;
        }
        return false;
    }

    @Override
    public void endSpellEffect(SpellEffect spellEffect) {
        if (!iterating){
            this.spellEffects.remove(spellEffect);
        } else {
            this.terminatedSpells.add(spellEffect);

        }
    }

    @Override
    public Collection<SpellEffect> getSpellEffects() {
        return this.spellEffects;
    }
}
