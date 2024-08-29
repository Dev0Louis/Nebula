package dev.louis.nebula.api.spell;

import net.minecraft.world.World;

public interface SpellCaster<T> {
    void castSpell(Spell<T> spell);
    World getWorld();
}
