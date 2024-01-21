package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;

public class ExampleSpell extends Spell {
    public ExampleSpell(SpellType<?> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {

    }
}
