package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Experimental
public abstract class SpellEffect {
    private final SpellEffectType<?> type;
    protected final LivingEntity entity;
    private boolean terminated;

    public SpellEffect(SpellEffectType<?> type, LivingEntity entity) {
        this.type = type;
        this.entity = entity;
    }



    public abstract void onStart();

    public abstract void tick();

    public abstract void onEnd();

    public boolean shouldContinue() {
        return !terminated;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    public void readNbt(NbtElement nbtElement) {

    }

    void terminate() {
        ((SpellEffectHolder) entity).endSpellEffect(this);
    }

    public SpellEffectType<?> getType() {
        return type;
    }

    public boolean canStart(Collection<SpellEffect> activeSpellEffects) {
        return activeSpellEffects.stream().noneMatch(spellEffect -> spellEffect.type.equals(this.type));
    }
}
