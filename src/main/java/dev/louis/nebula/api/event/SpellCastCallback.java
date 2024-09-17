package dev.louis.nebula.api.event;

import dev.louis.nebula.api.spell.quick.QuickSpell;
import dev.louis.nebula.api.spell.quick.QuickSpellSource;
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
        boolean allowSpellCast(QuickSpellSource<?> quickSpellSource, QuickSpell<?> quickSpell);

    }
    interface After {
        void onSpellCast(QuickSpellSource<?> quickSpellSource, QuickSpell<?> quickSpell);
    }
}
