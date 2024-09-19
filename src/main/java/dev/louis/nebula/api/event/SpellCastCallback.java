package dev.louis.nebula.api.event;

import dev.louis.nebula.api.spell.Spell;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SpellCastCallback {
    Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, (listeners) -> (spell) -> {
                for (Before event : listeners) {
                    var disallowed = !event.allowSpellCast(spell);
                    if (disallowed) return false;
                }

                return true;
            }
    );

    Event<After> AFTER = EventFactory.createArrayBacked(After.class, (listeners) -> (spell) -> {
                for (After event : listeners) {
                    event.onSpellCast(spell);
                }
            }
    );

    interface Before {
        boolean allowSpellCast(Spell quickSpellSource);
    }

    interface After {
        void onSpellCast(Spell quickSpell);
    }
}
