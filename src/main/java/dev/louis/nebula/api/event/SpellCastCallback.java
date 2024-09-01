package dev.louis.nebula.api.event;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SpellCastCallback {
    Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, (listeners) -> (spellCaster, spell) -> {
                for (Before event : listeners) {
                    var disallowed = !event.allowSpellCast(spellCaster, spell);
                    if (disallowed) return false;
                }

                return true;
            }
    );

    Event<After> AFTER = EventFactory.createArrayBacked(After.class, (listeners) -> (spellCaster, spell) -> {
                for (After event : listeners) {
                    event.onSpellCast(spellCaster, spell);
                }
            }
    );

    interface Before {
        boolean allowSpellCast(SpellSource<?> spellSource, Spell<?> spell);

    }
    interface After {
        void onSpellCast(SpellSource<?> spellSource, Spell<?> spell);
    }
}
