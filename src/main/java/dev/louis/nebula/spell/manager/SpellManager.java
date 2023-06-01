package dev.louis.nebula.spell.manager;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Set;

public interface SpellManager {
    void tick();

    void setCastableSpells(Set<SpellType<? extends Spell>> castableSpells);
    Set<SpellType<? extends Spell>> getCastableSpells();
    void addCastableSpell(SpellType<? extends Spell> spellType);
    void addCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells);
    void updateCastableSpell(Map<SpellType<? extends Spell>, Boolean> castableSpells);
    void removeCastableSpell(SpellType<? extends Spell> spellType);
    void removeCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells);
    void copyFrom(ServerPlayerEntity oldPlayer, boolean alive);
    boolean doesKnow(SpellType<? extends Spell> spellType);
    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    @FunctionalInterface
    public static interface Factory<T extends SpellManager> {
        T createPlayerSpellKnowledgeManager(PlayerEntity player);
    }
}
