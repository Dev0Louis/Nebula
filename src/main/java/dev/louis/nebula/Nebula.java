package dev.louis.nebula;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.entrypoint.EntityManaPoolEntrypoint;
import dev.louis.nebula.api.mana.manager.ManaManagerHolder;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.api.spell.effect.SpellEffects;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import dev.louis.nebula.mana.CreativeInfiniteManaSource;
import dev.louis.nebula.api.mana.manager.ServerManaManager;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.ManaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Map<EntityManaPoolType, Integer> poolTypePriorityMap;


    @Override
    public void onInitialize() {
        NebulaCommand.init();
        SpellEffects.init();
        this.registerPacketReceivers();
        LOGGER.info("Nebula has been initialized.");
        EntityManaPoolRegistererImpl alternativeManaSourceRegisterer = EntityManaPoolRegistererImpl.INSTANCE;
        alternativeManaSourceRegisterer.register(Identifier.of(MOD_ID, "creative"), CreativeInfiniteManaSource.TYPE);

        FabricLoader.getInstance().invokeEntrypoints(
                "entityManaPool",
                EntityManaPoolEntrypoint.class,
                (entrypoint -> entrypoint.registerEntityManaPool(alternativeManaSourceRegisterer))
        );

        ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, keepEquipment) ->
                ((ServerManaManager) ManaManagerHolder.getManaManager(previous)).copyFrom((ServerManaManager) ManaManagerHolder.getManaManager(converted))
        );

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(MOD_ID, "entity_mana_pool");
            }

            @Override
            public void reload(ResourceManager manager) {
                // Clear Caches Here
                manager.findResources("entity_mana_pool", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
                    try {
                        var string = id.toString().replace("entity_mana_pool/","");
                        var manaPoolId = Identifier.tryParse(string.substring(0, string.length() - 5));

                        var data = JsonParser.parseReader(new InputStreamReader(resource.getInputStream())).getAsJsonObject();
                        var enabled = data.get("enabled").getAsBoolean();
                        var priority = data.get("priority").getAsInt();
                        System.out.println(enabled);
                        System.out.println(priority);
                    } catch (IOException e) {
                        LOGGER.error("An error occured while loading data {}", id, e);
                    }
                });

            }
        });
    }

    /*private void cursedEntrypointOrdering() throws IllegalAccessException, InvocationTargetException {
        var loader = FabricLoader.getInstance();
        var entrypointStorageField = Arrays.stream(((FabricLoaderImpl) loader).getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(EntrypointStorage.class))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple fields found! " + a + ", " + b);
                }).get();
        entrypointStorageField.setAccessible(true);
        var entrypointStorage = (EntrypointStorage) entrypointStorageField.get(loader);
        var entryMapField = Arrays.stream(entrypointStorage.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(Map.class))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple fields found! " + a + ", " + b);
                }).get();
        entryMapField.setAccessible(true);
        var entryMap = (Map<String, List<Object>>) entryMapField.get(entrypointStorage);
        var entrypoints = new ArrayList<>(entryMap.get("entityManaPool"));

        entrypoints.sort(Comparator.comparingInt(value -> {
            var getOrCreateMethod = Arrays.stream(value.getClass().getDeclaredMethods())
                    .filter(method -> method.getName().equals("getOrCreate"))
                    .reduce((a, b) -> {
                        throw new IllegalStateException("Multiple fields found! " + a + ", " + b);
                    }).get();
            getOrCreateMethod.setAccessible(true);
            try {
                return ((EntityManaPoolRegisteringEntrypoint) getOrCreateMethod.invoke(value, EntityManaPoolRegisteringEntrypoint.class)).getPriority();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }));

        entryMap.put("entityManaPool", entrypoints);
    }*/

    private void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(ManaPayload.ID, ManaPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StartSpellEffectPayload.ID, StartSpellEffectPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StopSpellEffectPayload.ID, StopSpellEffectPayload.CODEC);
    }
}

