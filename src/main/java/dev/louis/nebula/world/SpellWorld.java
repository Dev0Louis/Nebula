package dev.louis.nebula.world;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.world.chunk.Chunk;

public interface SpellWorld {
    boolean nebula$startSpell(Chunk chunk, Spell spell);

    boolean nebula$stopSpell(int id);

    boolean nebula$removeSpell(int id);

    Spell nebula$getSpell(int id);
}
