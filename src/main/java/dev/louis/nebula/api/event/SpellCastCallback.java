package dev.louis.nebula.api.event;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellCaster;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SpellCastCallback {
    Event<SpellCastCallback> EVENT = EventFactory.createArrayBacked(SpellCastCallback.class, (listeners) -> (spellCaster, spell) -> {
                for (SpellCastCallback event : listeners) {
                    var disallowed = !event.allowSpellCast(spellCaster, spell);
                    if (disallowed) return false;
                }

                return true;
            }
    );

    boolean allowSpellCast(SpellCaster<?> spellCaster, Spell<?> spell);

    interface After {
        Event<SpellCastCallback.After> EVENT = EventFactory.createArrayBacked(SpellCastCallback.After.class, (listeners) -> (spellCaster, spell) -> {
                    for (SpellCastCallback.After event : listeners) {
                        event.spellCast(spellCaster, spell);
                    }
         }
        );
        void spellCast(SpellCaster<?> spellCaster, Spell<?> spell);
    }
}
