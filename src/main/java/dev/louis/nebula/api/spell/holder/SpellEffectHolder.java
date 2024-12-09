package dev.louis.nebula.api.spell.holder;

import dev.louis.nebula.api.spell.effect.SpellEffect;
import net.minecraft.entity.LivingEntity;

import java.util.Collection;

public interface SpellEffectHolder {
    default boolean startSpellEffect(SpellEffect spellEffect) {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }
    default void stopSpellEffect(SpellEffect spellEffect) {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }
    default Collection<SpellEffect> getSpellEffects() {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }

    static void startSpellEffect(LivingEntity livingEntity, SpellEffect spellEffect) {
        livingEntity.startSpellEffect(spellEffect);
    }

    static void stopSpellEffect(LivingEntity livingEntity, SpellEffect spellEffect) {
        livingEntity.startSpellEffect(spellEffect);
    }

    static Collection<SpellEffect> getSpellEffects(LivingEntity livingEntity) {
        return livingEntity.getSpellEffects();
    }
}
