package dev.louis.nebula.spell;

import dev.louis.nebula.api.spell.Spell;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.function.Consumer;

// Slightly inspired by a quick glance at EntityList
public class SpellSet {
    private final Int2ObjectMap<Spell<?>> currentSpells = new Int2ObjectLinkedOpenHashMap<>();
    
    public void addSpell(Spell<?> spell) {
        currentSpells.put(spell.getId(), spell);
    }

    public void removeSpell(int id) {
        currentSpells.remove(id);
    }

    public void forEach(Consumer<Spell<?>> consumer) {
        currentSpells.forEach((id, spell) -> consumer.accept(spell));
    }
}
