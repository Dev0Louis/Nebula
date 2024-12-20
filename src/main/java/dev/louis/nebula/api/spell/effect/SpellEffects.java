package dev.louis.nebula.api.spell.effect;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SpellEffects {
    public static final RegistryKey<Registry<SpellEffect>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_effect"));
    public static final SimpleRegistry<SpellEffect> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<SpellEffect>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(REGISTRY_KEY);


    private SpellEffects() {

    }


    public static <T extends SpellEffect> T register(Identifier id, Factory<T> factory) {
        return Registry.register(
                REGISTRY,
                id,
                factory.create(id)
        );
    }

    public interface Factory<T extends SpellEffect> {
        T create(Identifier id);
    }

    public static void init() {

    }
}
