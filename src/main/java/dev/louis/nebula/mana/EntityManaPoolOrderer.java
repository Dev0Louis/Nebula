package dev.louis.nebula.mana;

import com.google.gson.JsonParser;
import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static dev.louis.nebula.Nebula.MOD_ID;

public class EntityManaPoolOrderer {
    public static final Data DEFAULT = new Data(true, 1000);
    public static int TRUTH = 0;
    public static Map<EntityManaPoolType, Data> poolTypePriorityMap = new HashMap<>();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(MOD_ID, "entity_mana_pool");
            }

            @Override
            public void reload(ResourceManager manager) {
                // Clear caches and advance the truth.
                TRUTH++;
                poolTypePriorityMap.clear();
                manager.findResources("entity_mana_pool", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
                    try {
                        var string = id.toString().replace("entity_mana_pool/","");
                        var manaPoolId = Identifier.tryParse(string.substring(0, string.length() - 5));
                        var data = JsonParser.parseReader(new InputStreamReader(resource.getInputStream())).getAsJsonObject();
                        var enabled = data.get("enabled").getAsBoolean();
                        var priority = data.get("priority").getAsInt();
                        EntityManaPoolRegistererImpl.REGISTRY.getOptionalValue(manaPoolId).ifPresent(type -> {
                            poolTypePriorityMap.put(type, new Data(enabled, priority));
                        });
                        System.out.println(poolTypePriorityMap);
                    } catch (IOException e) {
                        Nebula.LOGGER.error("An error occured while loading data {}", id, e);
                    }
                });

            }
        });
    }

    public record Data(boolean enabled, int priority) {

    }

    public static Data getData(EntityManaPoolType type) {
        return poolTypePriorityMap.getOrDefault(type, DEFAULT);
    }
}
