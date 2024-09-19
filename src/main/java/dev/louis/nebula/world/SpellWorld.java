package dev.louis.nebula.world;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface SpellWorld {
    boolean nebula$startSpell(Chunk chunk, Spell spell);

    boolean nebula$stopSpell(int id);

    Collection<Spell> nebula$takeSpells(Chunk chunk);

    Spell nebula$getSpell(int id);
}
