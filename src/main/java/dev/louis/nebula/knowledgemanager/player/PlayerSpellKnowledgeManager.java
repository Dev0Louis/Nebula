package dev.louis.nebula.knowledgemanager.player;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Set;

public interface PlayerSpellKnowledgeManager {
    Set<SpellType<? extends Spell>> getCastableSpells();

    void setCastableSpells(Set<SpellType<? extends Spell>> castableSpells);

    void addCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells);
    void updateCastableSpell(Map<SpellType<? extends Spell>, Boolean> castableSpells);
    void removeCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells);

    void copyFrom(ServerPlayerEntity oldPlayer, boolean alive);

    boolean doesKnow(SpellType<? extends Spell> spellType);

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);
}
