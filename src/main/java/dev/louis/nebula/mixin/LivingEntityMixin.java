package dev.louis.nebula.mixin;

import com.google.common.collect.ImmutableList;
import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.nebula.constants.NbtConstants;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static dev.louis.nebula.constants.NbtConstants.*;

@SuppressWarnings({"AddedMixinMembersNamePattern", "UnreachableCode"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder, SpellEffectHolder {
    @Shadow public abstract float getHealth();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    protected NebulaManaManager manaManager;
    @Unique
    protected List<SpellEffect> spellEffects = new ArrayList<>();


    // We init just after health was set.
    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V", shift = At.Shift.AFTER)
    )
    public void lateManaManagerInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        manaManager = NebulaManaManager.createManaManager((LivingEntity) (Object) this);
        manaManager.setMana(this.getHealth());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);

        this.manaManager.writeNbt(nebulaNbt);

        NbtList spellEffectsNbt = new NbtList();
        spellEffects.forEach((spellEffect) -> {
            var spellEffectNbt = new NbtCompound();
            spellEffectNbt.putString(ID, spellEffect.getRegistryEntry().getIdAsString());
            spellEffectNbt.put(DATA, spellEffect.writeNbt(new NbtCompound()));
            spellEffectsNbt.add(spellEffectNbt);
        });
        nebulaNbt.put(SPELL_EFFECTS, spellEffectsNbt);

        nbt.put(NbtConstants.NEBULA, nebulaNbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        this.manaManager.readNbt(nebulaNbt);

        var nbtList = nebulaNbt.getList(SPELL_EFFECTS, NbtCompound.END_TYPE);
        var spellEffects = new ArrayList<SpellEffect>(nbtList.size());

        for (NbtElement nbtElement : nbtList) {
            var spellEffectNbt = (NbtCompound) nbtElement;
            var id = Identifier.tryParse(spellEffectNbt.getString(ID));
            SpellEffectType.REGISTRY.getEntry(id).ifPresentOrElse(spellEffectType -> {
                var spellEffect = spellEffectType.value().factory().create((LivingEntity) (Object) this);
                spellEffect.readNbt(spellEffectNbt.getCompound(DATA));
                spellEffects.add(spellEffect);
            }, () -> {
                Nebula.LOGGER.warn("Spell effect {} wasn't registered! This can happen if you remove or update Mods!", id);
            });

        }

        this.spellEffects = spellEffects;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManagerAndSpellEffects(CallbackInfo ci) {
        this.manaManager.tick();

        List<SpellEffect> registryEntries = new LinkedList<>();
        for (SpellEffect spellEffect : spellEffects) {
            spellEffect.age++;
            if (!spellEffect.shouldContinue()) {
                registryEntries.add(spellEffect);
                spellEffect.onEnd();
                continue;
            }
            spellEffect.tick();
        }
        registryEntries.forEach(this::removeSpellEffect);
    }


    @Inject(
            method = "drop",
            at = @At("RETURN")
    )
    public void voidManaAtDeath(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        this.getManaManager().setMana(0);
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
        if (spellEffect.canStart(this.getSpellEffects())) {
            this.spellEffects.add(spellEffect);
            spellEffect.onStart();
            return true;
        }
        return false;
    }

    @Override
    public void stopSpellEffect(SpellEffect spellEffect) {
        var existed = removeSpellEffect(spellEffect);
        if (existed) spellEffect.onEnd();
    }

    @Override
    public Collection<SpellEffect> getSpellEffects() {
        return ImmutableList.copyOf(this.spellEffects);
    }

    // Internal

    @Unique
    protected boolean removeSpellEffect(SpellEffect spellEffect) {
        return this.spellEffects.remove(spellEffect);
    }

}
