package dev.louis.nebula.api.spell;

import com.mojang.serialization.Codec;
import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record TickingSpellType<T extends TickingSpell<?>>(Identifier id, BiConsumer<T, NbtCompound> encoder, Function<NbtCompound, Optional<T>> decoder) {


    @Unique
    private static final String SPELL_DATA = "SpellData";

    public static final RegistryKey<Registry<TickingSpellType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "ticking_spell_type"));
    public static final Registry<TickingSpellType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();


    public static <T extends TickingSpell<?>> TickingSpellType<T> register(Identifier id, TickingSpellType<T> tickingSpellType) {
        return Registry.register(REGISTRY, id, tickingSpellType);
    }

    public static <T extends TickingSpell<?>> RegistryEntry.Reference<TickingSpellType<?>> registerReference(Identifier id, TickingSpellType<T> tickingSpellType) {
        return Registry.registerReference(REGISTRY, id, tickingSpellType);
    }
/*
            NbtCompound spellNbt = new NbtCompound();
            spellNbt.putString(SPELL_ID, tickingSpell.getType().id().toString());
 */
    public static <T extends TickingSpell<?>> void create(Identifier id, Codec<T> codec) {
        new TickingSpellType<T>(id, (tickingSpell, nbt) -> {
            codec.encodeStart(NbtOps.INSTANCE, tickingSpell).resultOrPartial(Nebula.LOGGER::error).ifPresent(nbtElement -> nbt.put(SPELL_DATA, nbtElement));
        }, (nbt) -> {
            return codec.parse(NbtOps.INSTANCE, nbt.get(SPELL_DATA)).resultOrPartial(Nebula.LOGGER::error);
        });
    }

    public static <T extends TickingSpell<?>> void create(Identifier id, BiConsumer<T, NbtCompound> encoder, Function<NbtCompound, Optional<T>> decoder) {
        new TickingSpellType<T>(id, encoder, decoder);
    }


    public static <T extends TickingSpell<?>> void createDataless(Identifier id, DatalessFactory<T> datalessFactory) {
        new TickingSpellType<T>(
                id,
                (spell, nbt) -> {},
                (nbt) -> Optional.of(datalessFactory.create())
        );
    }

    public interface DatalessFactory<T extends TickingSpell<?>> {
        T create();
    }
}
