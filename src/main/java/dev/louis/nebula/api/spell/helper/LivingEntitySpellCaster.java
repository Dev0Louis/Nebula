package dev.louis.nebula.api.spell.helper;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.entity.LivingEntity;

public interface LivingEntitySpellCaster {
    default void castSpell(Spell<LivingEntity> spell) {
        throw new UnsupportedOperationException();
    }
}
