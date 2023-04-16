package dev.louis.nebula.knowledgemanager;

import dev.louis.nebula.knowledgemanager.player.NebulaPlayerSpellKnowledgeManager;
import net.minecraft.entity.player.PlayerEntity;

public class NebulaSpellKnowledgeManager implements SpellKnowledgeManager<NebulaPlayerSpellKnowledgeManager> {

    @Override
    public NebulaPlayerSpellKnowledgeManager createPlayerSpellKnowledgeManager(PlayerEntity player) {
        return new NebulaPlayerSpellKnowledgeManager(player);
    }

    @Override
    public void setUp() {}
}
