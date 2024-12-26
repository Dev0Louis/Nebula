package dev.louis.nebula.entrypoint;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.entrypoint.ManaPoolRegisterer;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.mana.EntityManaPoolOrderer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.HashMap;

@ApiStatus.Internal
public final class EntityManaPoolRegistererImpl implements ManaPoolRegisterer {
    public static final RegistryKey<Registry<EntityManaPoolType>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "mana_pool"));
    public static final SimpleRegistry<EntityManaPoolType> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<EntityManaPoolType>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(REGISTRY_KEY);

    public static EntityManaPoolRegistererImpl INSTANCE = new EntityManaPoolRegistererImpl();



    private EntityManaPoolRegistererImpl() {
    }

    public void register(Identifier id, EntityManaPoolType type) {
        Registry.register(REGISTRY, id, type);
    }

    public HashMap<RegistryEntry<EntityManaPoolType>, EntityManaPool> createManaPool(LivingEntity entity) {
        HashMap<RegistryEntry<EntityManaPoolType>, EntityManaPool> map = new HashMap<>(REGISTRY.size());
        REGISTRY.streamEntries()
                .filter(ref -> EntityManaPoolOrderer.getData(ref.value()).enabled())
                .sorted(Comparator.comparingInt(ref -> EntityManaPoolOrderer.getData(ref.value()).priority()))
                .forEach(ref -> {
            var entry = REGISTRY.getOptional(ref.registryKey()).orElseThrow();
            var value = ref.value();
            var pool = value.entityManaPoolFactory().create(entity);
            if (pool == null) return;
            map.put(entry, pool);
        });
        return map;
    }
}
