package dev.louis.nebula.spell;

import dev.louis.nebula.api.NebulaUser;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static dev.louis.nebula.Nebula.NebulaRegistries.SPELL_TYPE;

public class SpellType<T extends Spell<?>> {

    private final SpellFactory<T> factory;
    private final int manaCost;

    /*
    EXAMPLE:
    public static SpellType<NullSpell> NULL_SPELL = register(new Identifier("nebula", "null_spell"), Builder.create(NullSpell::new, Integer.MAX_VALUE));
     */

    public static <T extends Spell<?>> SpellType<T> register(String id, Builder<T> type) {
        return register(Identifier.tryParse(id), type);
    }
    public static <T extends Spell<?>> SpellType<T> register(Identifier id, Builder<T> type) {
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

    public Identifier getId() {
        return SPELL_TYPE.getId(this);
    }


    public SpellType(SpellFactory<T> factory, int manaCost) {
        this.factory = factory;
        this.manaCost = manaCost;
    }


    public boolean isCastable(NebulaUser player) {
        return player.getSpellManager().isCastable(this);
    }

    public boolean hasEnoughMana(NebulaUser player) {
        return ((player.getManaManager().getMana() - getManaCost()) >= 0);
    }

    public boolean hasLearned(NebulaUser player) {
        return player.getSpellManager().hasLearned(this);
    }

    public int getManaCost() {
        return manaCost;
    }


    public T create(NebulaUser caster) {
        return this.factory.create(this, caster);
    }

    public static class Builder<T extends Spell<?>> {
        private final SpellFactory<T> factory;
        private final int manaCost;

        private Builder(SpellFactory<T> factory, int manaCost) {
            this.factory = factory;
            this.manaCost = manaCost;
        }

        public static <T extends Spell<?>> Builder<T> create(SpellFactory<T> factory, int manaCost) {
            return new Builder<T>(factory, manaCost);
        }

        public SpellType<T> build() {
            return new SpellType<T>(this.factory, manaCost);
        }


    }
    public static interface SpellFactory<T extends Spell<?>> {
        public T create(SpellType<T> spellType, NebulaUser caster);
    }
}
