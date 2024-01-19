package dev.louis.nebula.api.event;

import dev.louis.nebula.api.manager.spell.Spell;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SpellCastCallback {
    Event<SpellCastCallback> EVENT = EventFactory.createArrayBacked(SpellCastCallback.class, (listeners) -> (player, spell) -> {
                for (SpellCastCallback event : listeners) {
                    ActionResult result = event.interact(player, spell);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, Spell spell);
}
