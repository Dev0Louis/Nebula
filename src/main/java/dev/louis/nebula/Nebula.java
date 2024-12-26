package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.entrypoint.EntityManaPoolEntrypoint;
import dev.louis.nebula.api.mana.manager.ManaManagerHolder;
import dev.louis.nebula.api.spell.effect.SpellEffects;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import dev.louis.nebula.mana.CreativeInfiniteManaSource;
import dev.louis.nebula.api.mana.manager.ServerManaManager;
import dev.louis.nebula.mana.EntityManaPoolOrderer;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.ManaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();


    @Override
    public void onInitialize() {
        NebulaCommand.init();
        SpellEffects.init();
        this.registerPacketReceivers();
        LOGGER.info("Nebula has been initialized.");
        EntityManaPoolRegistererImpl alternativeManaSourceRegisterer = EntityManaPoolRegistererImpl.INSTANCE;
        alternativeManaSourceRegisterer.register(Identifier.of(MOD_ID, "creative"), CreativeInfiniteManaSource.TYPE);
        EntityManaPoolOrderer.init();
        FabricLoader.getInstance().invokeEntrypoints(
                "entityManaPool",
                EntityManaPoolEntrypoint.class,
                (entrypoint -> entrypoint.registerEntityManaPool(alternativeManaSourceRegisterer))
        );

        ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, keepEquipment) ->
                ((ServerManaManager) ManaManagerHolder.getManaManager(previous)).copyFrom((ServerManaManager) ManaManagerHolder.getManaManager(converted))
        );
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

