package dev.louis.nebula.event;

import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SpellKnowledgeAddCallback {
    Event<SpellKnowledgeAddCallback> EVENT = EventFactory.createArrayBacked(SpellKnowledgeAddCallback.class, (listeners) -> (player, spellType) -> {
                for (SpellKnowledgeAddCallback event : listeners) {
                    ActionResult result = event.interact(player, spellType);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, SpellType spellType);
}
