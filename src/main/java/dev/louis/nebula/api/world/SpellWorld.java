package dev.louis.nebula.api.world;

import dev.louis.nebula.api.spell.Spell;

public interface SpellWorld {
    boolean startSpell(Spell spell);

    boolean stopSpell(int id);

    Spell getSpell(int id);
}
