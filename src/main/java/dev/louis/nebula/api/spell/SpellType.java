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
    private static final RegistryKey<Registry<SpellType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(new Identifier(Nebula.MOD_ID, "spell_type"));
    public static final SimpleRegistry<SpellType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    private final SpellFactory<T> factory;
    private final int manaCost;
    private final boolean allowsMultipleCasts;
    private final boolean needLearning;
    private final SpellCastingValidator castabilityFunction;

    @ApiStatus.Internal
    public SpellType(SpellFactory<T> factory, int manaCost, boolean allowsMultipleCasts, boolean needLearning, SpellCastingValidator castabilityFunction) {
        this.factory = factory;
        this.manaCost = manaCost;
        this.allowsMultipleCasts = allowsMultipleCasts;
        this.needLearning = needLearning;
        this.castabilityFunction = castabilityFunction;
    }

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

    public boolean isCastable(PlayerEntity player) {
        return castabilityFunction.isCastable(this, player);
    }

    public boolean allowsMultipleCasts() {
        return allowsMultipleCasts;
    }

    public boolean needsLearning() {
        return needLearning;
    }

    public boolean hasLearned(PlayerEntity player) {
        return player.getSpellManager().hasLearned(this);
    }

    public int getManaCost() {
        return manaCost;
    }

    public T create() {
        return this.factory.create(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{id=" + this.getId() + ", manaCost=" + this.getManaCost() + "}";
    }

    public static class Builder<T extends Spell> {
        private final SpellFactory<T> factory;
        private final int manaCost;
        private boolean allowsMultipleCasts;
        private boolean needsLearning = true;
        private SpellCastingValidator castabilityFunction = SpellCastingValidator.DEFAULT;

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
        public Builder<T> allowMultipleCasts() {
            this.allowsMultipleCasts = true;
            return this;
        }

        public Builder<T> needsLearning(boolean needsLearning) {
            this.needsLearning = needsLearning;
            return this;
        }

        public Builder<T> castabilityFunction(SpellCastingValidator castabilityFunction) {
            this.castabilityFunction = castabilityFunction;
            return this;
        }

        public SpellType<T> build() {
            return new SpellType<>(this.factory, manaCost, allowsMultipleCasts, needsLearning, castabilityFunction);
        }
    }

    @FunctionalInterface
    public interface SpellFactory<T extends Spell> {
        T create(SpellType<T> spellType);
    }

    @FunctionalInterface
    public interface SpellCastingValidator {
        SpellCastingValidator ALWAYS_CASTABLE = (spellType, player) -> true;
        SpellCastingValidator DEFAULT = (spellType, player) -> player.getManaManager().isCastable(spellType) && player.getSpellManager().isCastable(spellType);
        SpellCastingValidator NEVER_CASTABLE = (spellType, player) -> false;

        boolean isCastable(SpellType<?> spellType, PlayerEntity player);

        default SpellCastingValidator and(SpellCastingValidator other) {
            return (spellType, player) -> this.isCastable(spellType, player) && other.isCastable(spellType, player);
        }

        default SpellCastingValidator or(SpellCastingValidator other) {
            return (spellType, player) -> this.isCastable(spellType, player) || other.isCastable(spellType, player);
        }
    }
}
