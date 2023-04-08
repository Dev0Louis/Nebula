package dev.louis.nebula.spell;

import dev.louis.nebula.api.NebulaPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static dev.louis.nebula.Nebula.NebulaRegistries.SPELL_TYPE;

public class SpellType<T extends Spell> {

    private final SpellFactory<T> factory;
    private final int manaCost;

    /*
    EXAMPLE:
    public static SpellType<NullSpell> NULL_SPELL = register(new Identifier("nebula", "null_spell"), Builder.create(NullSpell::new, Integer.MAX_VALUE));
     */

    public static <T extends Spell> SpellType<T> register(String id, Builder<T> type) {
        return register(Identifier.tryParse(id), type);
    }
    public static <T extends Spell> SpellType<T> register(Identifier id, Builder<T> type) {
        return Registry.register(SPELL_TYPE, id, type.build());
    }

    public static void init() {
    }

    public static Optional<SpellType<?>> get(String id) {
        return get(Identifier.tryParse(id));
    }
    public static Optional<SpellType<?>> get(Identifier id) {
        return SPELL_TYPE.getOrEmpty(id);
    }

    public static Identifier getId(SpellType<?> type) {
        return SPELL_TYPE.getId(type);
    }

    public static boolean isCastable(PlayerEntity player, SpellType<? extends Spell> spellType) {
        return isCastable(NebulaPlayer.access(player), spellType);
    }
    public static boolean isCastable(NebulaPlayer player, SpellType<? extends Spell> spellType) {
        return spellType.isCastable(player);
    }


    public static boolean hasEnoughMana(PlayerEntity player, SpellType<? extends Spell> spellType) {
        return hasEnoughMana(player, spellType);
    }
    public static boolean hasEnoughMana(NebulaPlayer player, SpellType<? extends Spell> spellType) {
        return spellType.hasEnoughMana(player);
    }


    public static boolean doesKnow(PlayerEntity player, SpellType<? extends Spell> spellType) {
        return doesKnow(NebulaPlayer.access(player), spellType);
    }
    public static boolean doesKnow(NebulaPlayer player, SpellType<? extends Spell> spellType) {
        return spellType.doesKnow(player);
    }


    public SpellType(SpellFactory<T> factory, int manaCost) {
        this.factory = factory;
        this.manaCost = manaCost;
    }


    public boolean isCastable(PlayerEntity player) {
        return isCastable(NebulaPlayer.access(player));
    }
    public boolean isCastable(NebulaPlayer player) {
        return doesKnow(player) && hasEnoughMana(player);
    }


    public boolean hasEnoughMana(PlayerEntity player) {
        return hasEnoughMana(NebulaPlayer.access(player));
    }
    public boolean hasEnoughMana(NebulaPlayer player) {
        return ((player.getMana() - getManaCost()) >= 0);
    }


    public boolean doesKnow(PlayerEntity player) {
        return doesKnow(NebulaPlayer.access(player));
    }
    public boolean doesKnow(NebulaPlayer player) {
        return player.getSpellKnowledgeManager().doesKnow(this);
    }


    public int getManaCost() {
        return manaCost;
    }


    public T create(PlayerEntity caster) {
        return this.factory.create(this, caster);
    }

    public static class Builder<T extends Spell> {
        private final SpellFactory<T> factory;
        private final int manaCost;

        private Builder(SpellFactory<T> factory, int manaCost) {
            this.factory = factory;
            this.manaCost = manaCost;
        }

        public static <T extends Spell> Builder<T> create(SpellFactory<T> factory, int manaCost) {
            return new Builder<T>(factory, manaCost);
        }

        public SpellType<T> build() {
            return new SpellType<T>(this.factory, manaCost);
        }


    }
    public static interface SpellFactory<T extends Spell> {
        public T create(SpellType<T> spellType, PlayerEntity caster);
    }
}
