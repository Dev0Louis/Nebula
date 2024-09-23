package dev.louis.nebula.api.spell.helper;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEntitySpellCaster {
    default void castSpell(Spell<PlayerEntity> spell) {
        throw new UnsupportedOperationException();
    }
}
