package dev.louis.nebula.api.mana.pool.entity;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record EntityManaPoolType(EntityManaPoolFactory entityManaPoolFactory) {
    public static final RegistryKey<Registry<EntityManaPoolType>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "mana_pool"));
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<EntityManaPoolType>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(REGISTRY_KEY);
    public static final SimpleRegistry<EntityManaPoolType> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static EntityManaPoolType create(EntityManaPoolFactory entityManaPoolFactory) {
        return new EntityManaPoolType(entityManaPoolFactory);
    }
}
