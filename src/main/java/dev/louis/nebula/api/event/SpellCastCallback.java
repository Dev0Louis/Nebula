package dev.louis.nebula.api.event;

import dev.louis.nebula.api.spell.Spell;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface SpellCastCallback {
    Event<SpellCastCallback> EVENT = EventFactory.createArrayBacked(SpellCastCallback.class, (listeners) -> (player, spell) -> {
                for (SpellCastCallback event : listeners) {
                    var disallowed = !event.allowSpellCast(player, spell);
                    if (disallowed) return false;
                }

                return true;
            }
    );

    boolean allowSpellCast(PlayerEntity player, Spell<?> spell);

    interface After {
        Event<SpellCastCallback.After> EVENT = EventFactory.createArrayBacked(SpellCastCallback.After.class, (listeners) -> (player, spell) -> {
                    for (SpellCastCallback.After event : listeners) {
                        event.spellCast(player, spell);
                    }
         }
        );
        void spellCast(PlayerEntity player, Spell<?> spell);
    }
}
