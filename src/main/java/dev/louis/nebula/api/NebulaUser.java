package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface NebulaUser {
    ManaManager getManaManager();
    void setManaManager(ManaManager manaManager);
    public SpellManager getSpellManager();
    public SpellManager setSpellManager(SpellManager spellManager);
    @NotNull
    static NebulaUser access(PlayerEntity player) {
        return (NebulaUser) player;
    }
}
