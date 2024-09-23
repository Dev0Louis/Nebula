package dev.louis.nebula.api.spell.holder;

import dev.louis.nebula.api.spell.SpellEffect;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SpellEffectHolder {
    boolean startSpellEffect(SpellEffect spellEffect);
    void endSpellEffect(SpellEffect spellEffect);

    static void startSpellEffect(LivingEntity livingEntity, SpellEffect spellEffect) {
        livingEntity.startSpellEffect(spellEffect);
    }

    static void endSpellEffect(LivingEntity livingEntity, SpellEffect spellEffect) {
        livingEntity.startSpellEffect(spellEffect);
    }
}
