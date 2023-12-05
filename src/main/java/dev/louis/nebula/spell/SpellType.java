package dev.louis.nebula.spell;

import dev.louis.nebula.Nebula;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SpellType<T extends Spell> {
    private final SpellFactory<T> factory;
    private final int manaCost;

    public SpellType(SpellFactory<T> factory, int manaCost) {
        this.factory = factory;
        this.manaCost = manaCost;
    }

    public static void init() {
    }

    public static <T extends Spell> SpellType<T> register(Identifier id, Builder<T> type) {
        return Registry.register(Nebula.SPELL_REGISTRY, id, type.build());
    }

    public static Optional<SpellType<?>> get(Identifier id) {
        return Nebula.SPELL_REGISTRY.getOrEmpty(id);
    }

    public Identifier getId() {
        return Nebula.SPELL_REGISTRY.getId(this);
    }

    public boolean isCastable(PlayerEntity player) {
        return player.getSpellManager().isCastable(this);
    }

    public boolean hasLearned(PlayerEntity player) {
        return player.getSpellManager().hasLearned(this);
    }

    public int getManaCost() {
        return manaCost;
    }

    public T create(PlayerEntity caster) {
        return this.factory.create(this, caster);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{id=" + this.getId() + ", manaCost=" + this.getManaCost() + "}";
    }

    public static class Builder<T extends Spell> {
        private final SpellFactory<T> factory;
        private final int manaCost;

        private Builder(SpellFactory<T> factory, int manaCost) {
            this.factory = factory;
            this.manaCost = manaCost;
        }

        public static <T extends Spell> Builder<T> create(SpellFactory<T> factory, int manaCost) {
            return new Builder<>(factory, manaCost);
        }

        public SpellType<T> build() {
            return new SpellType<>(this.factory, manaCost);
        }
    }

    public interface SpellFactory<T extends Spell> {
        T create(SpellType<T> spellType, PlayerEntity caster);
    }
}
