package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

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

    public void writeNbt() {

    }

    public void readNbt() {

    }

    void terminate() {
        ((SpellEffectHolder) entity).endSpellEffect(this);
    }

    public SpellEffectType<?> getType() {
        return type;
    }
}
