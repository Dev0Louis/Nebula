package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface NebulaPlayer {
    ManaManager getManaManager();
    void setManaManager(ManaManager manaManager);
    public SpellManager getSpellManager();
    public SpellManager setSpellManager(SpellManager spellManager);
    @NotNull
    static NebulaPlayer access(PlayerEntity player) {
        return (NebulaPlayer) player;
    }
}
