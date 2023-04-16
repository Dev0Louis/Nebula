package dev.louis.nebula.knowledgemanager;

import dev.louis.nebula.knowledgemanager.player.PlayerSpellKnowledgeManager;
import net.minecraft.entity.player.PlayerEntity;

public interface SpellKnowledgeManager<T extends PlayerSpellKnowledgeManager> {
    T createPlayerSpellKnowledgeManager(PlayerEntity player);
    void setUp();
}
