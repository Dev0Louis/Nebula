package dev.louis.nebula.api.spell.holder;

import dev.louis.nebula.api.spell.effect.SpellEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;

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

    default boolean canStartSpellEffect(ServerWorld world, SpellEffect spellEffect) {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }

    @ApiStatus.Internal
    default HashMap<SpellEffect, Integer> nebula$getSpellEffectsInternal() {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }
    @ApiStatus.Internal
    default void nebula$setSpellEffectsInternal(HashMap<SpellEffect, Integer> map) {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }

    @ApiStatus.Internal
    default void nebula$onSpellEffectStoppedInternal(SpellEffect spellEffect) {
        throw new UnsupportedOperationException("BEEP BOOP ME MIXIN!");
    }

    @ApiStatus.Internal
    default void nebula$onSpellEffectStartInternal(SpellEffect spellEffect) {
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
