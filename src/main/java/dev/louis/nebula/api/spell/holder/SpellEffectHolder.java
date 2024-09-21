package dev.louis.nebula.api.spell.holder;

import dev.louis.nebula.api.spell.SpellEffect;

public interface SpellEffectHolder {
    void startSpellEffect(SpellEffect spellEffect);
    void endSpellEffect(SpellEffect spellEffect);
}
