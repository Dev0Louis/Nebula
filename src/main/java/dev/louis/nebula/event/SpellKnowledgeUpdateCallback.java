package dev.louis.nebula.event;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.Map;

public interface SpellKnowledgeUpdateCallback {
    Event<SpellKnowledgeUpdateCallback> EVENT = EventFactory.createArrayBacked(SpellKnowledgeUpdateCallback.class, (listeners) -> (player, castableSpells) -> {
                for (SpellKnowledgeUpdateCallback event : listeners) {
                    ActionResult result = event.interact(player, castableSpells);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, Map<SpellType<? extends Spell>, Boolean> castableSpells);
}