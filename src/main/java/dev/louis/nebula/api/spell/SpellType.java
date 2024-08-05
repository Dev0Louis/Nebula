package dev.louis.nebula.api.spell;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

public class SpellType<T extends Spell> {
    public static final RegistryKey<Registry<SpellType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_type"));
    public static final SimpleRegistry<SpellType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    private final SpellFactory<T> spellFactory;
    private final int manaCost;
    private final boolean allowsParallelCasts;
    private final boolean needsLearning;

    @ApiStatus.Internal
    public SpellType(SpellFactory<T> spellFactory, int manaCost, boolean allowsParallelCasts, boolean needsLearning) {
        this.spellFactory = spellFactory;
        this.manaCost = manaCost;
        this.allowsParallelCasts = allowsParallelCasts;
        this.needsLearning = needsLearning;
    }

    @ApiStatus.Internal
    public static void init() {
    }

    public static <T extends Spell> SpellType<T> register(Identifier id, Builder<T> type) {
        return Registry.register(REGISTRY, id, type.build());
    }

    public static Optional<SpellType<?>> get(Identifier id) {
        return REGISTRY.getOrEmpty(id);
    }

    public Identifier getId() {
        return REGISTRY.getId(this);
    }

    /**
     * This should be used to check if the player can cast the spell.
     */
    public boolean isCastable(PlayerEntity player) {
        return true; //TODO: IMPLEMENT
    }

    /**
     * Utility method for checking if the spell has been learned by the given player.
     * @param player Player to check.
     * @return If the spell has been learned.
     */
    public boolean isLearnedBy(PlayerEntity player) {
        return true; //TODO: IMPLEMENT
    }

    public boolean allowsParallelCasts() {
        return allowsParallelCasts;
    }

    public boolean needsLearning() {
        return needsLearning;
    }

    public int getManaCost() {
        return manaCost;
    }

    public T create(PlayerEntity caster) {
        return this.spellFactory.create(this, caster);
    }

    @Override
    public String toString() {
        return "SpellType{" +
                "spellFactory=" + spellFactory +
                ", manaCost=" + manaCost +
                ", allowsParallelCasts=" + allowsParallelCasts +
                ", needLearning=" + needsLearning +
                '}';
    }

    public static class Builder<T extends Spell> {
        private final SpellFactory<T> factory;
        private final int manaCost;
        private boolean allowParallelCast;
        private boolean needsLearning = true;

        private Builder(SpellFactory<T> factory, int manaCost) {
            this.factory = factory;
            this.manaCost = manaCost;
        }

        public static <T extends Spell> Builder<T> create(SpellFactory<T> factory, int manaCost) {
            return new Builder<>(factory, manaCost);
        }

        /**
         * Allows players to cast a spell multiple times at the same time. <br>
         * That means that a spell could be cast while it is already ticking
         */
        public Builder<T> parallelCast() {
            this.allowParallelCast = true;
            return this;
        }

        /**
         * Set if the spell needs to be learned before it can be cast.
         */
        public Builder<T> needsLearning(boolean needsLearning) {
            this.needsLearning = needsLearning;
            return this;
        }

        public SpellType<T> build() {
            return new SpellType<>(
                    this.factory,
                    this.manaCost,
                    this.allowParallelCast,
                    this.needsLearning
            );
        }
    }

    @FunctionalInterface
    public interface SpellFactory<T extends Spell> {
        T create(SpellType<T> spellType, PlayerEntity caster);
    }
}
