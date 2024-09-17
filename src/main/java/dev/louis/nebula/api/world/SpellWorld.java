package dev.louis.nebula.api.world;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.world.chunk.Chunk;

public interface SpellWorld {
    boolean startSpell(Chunk chunk, Spell spell);

    boolean stopSpell(int id);

    Spell getSpell(int id);
}
