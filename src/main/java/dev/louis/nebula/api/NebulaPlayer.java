package dev.louis.nebula.api;

import dev.louis.nebula.knowledgemanager.player.PlayerSpellKnowledgeManager;
import dev.louis.nebula.manamanager.player.PlayerManaManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface NebulaPlayer {
    PlayerManaManager getPlayerManaManager();
    void setPlayerManaManager(PlayerManaManager manaManager);
    public int getMana();
    public void setMana(int mana);
    public PlayerSpellKnowledgeManager getSpellKnowledge();

    @NotNull
    static NebulaPlayer access(PlayerEntity player) {
        return (NebulaPlayer) player;
    }
}
