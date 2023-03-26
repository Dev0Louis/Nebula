package dev.louis.nebula.event;

import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SpellKnowledgeRemoveCallback {
    Event<SpellKnowledgeRemoveCallback> EVENT = EventFactory.createArrayBacked(SpellKnowledgeRemoveCallback.class, (listeners) -> (player, removedSpellType) -> {
                for (SpellKnowledgeRemoveCallback event : listeners) {
                    ActionResult result = event.interact(player, removedSpellType);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, SpellType removedSpellType);
}
