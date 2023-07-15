package dev.louis.nebula.event;

import dev.louis.nebula.api.NebulaUser;
import dev.louis.nebula.spell.Spell;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface SpellCastCallback {
    Event<SpellCastCallback> EVENT = EventFactory.createArrayBacked(SpellCastCallback.class, (listeners) -> (nebulaUser, spell) -> {
                for (SpellCastCallback event : listeners) {
                    ActionResult result = event.interact(nebulaUser, spell);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(NebulaUser nebulaUser, Spell<?> spell);
}
