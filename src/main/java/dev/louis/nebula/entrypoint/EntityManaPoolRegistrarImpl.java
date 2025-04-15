package dev.louis.nebula.entrypoint;

import dev.louis.nebula.api.entrypoint.EntityManaPoolRegistrar;
import dev.louis.nebula.api.mana.pool.entity.ManaAttachment;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.mana.EntityManaPoolOrderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.Map;

@ApiStatus.Internal
public final class EntityManaPoolRegistrarImpl implements EntityManaPoolRegistrar {
    public static EntityManaPoolRegistrarImpl INSTANCE = new EntityManaPoolRegistrarImpl();

    private EntityManaPoolRegistrarImpl() {
    }

    public void register(Identifier id, EntityManaPoolType type) {
        Registry.register(EntityManaPoolType.REGISTRY, id, type);
    }

    public Map<RegistryEntry<EntityManaPoolType>, ManaAttachment> createManaPool(ServerPlayerEntity player) {
        //This needs to be an ordered ist as to ensure that the order of mana pools is constant.
        Map<RegistryEntry<EntityManaPoolType>, ManaAttachment> map = new Object2ObjectLinkedOpenHashMap<>(EntityManaPoolType.REGISTRY.size());
        EntityManaPoolType.REGISTRY.streamEntries()
                .filter(ref -> EntityManaPoolOrderer.getData(ref.value()).enabled())
                .sorted(Comparator.comparingInt(ref -> EntityManaPoolOrderer.getData(ref.value()).priority()))
                .forEachOrdered(ref -> {
                    var entry = EntityManaPoolType.REGISTRY.getOptional(ref.registryKey()).orElseThrow();
                    var value = ref.value();
                    var pool = value.playerManaPoolFactory().create(player);
                    if (pool == null) return;
                    map.put(entry, pool);
                });
        return map;
    }
}
