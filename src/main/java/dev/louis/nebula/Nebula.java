package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.entrypoint.AlternativeManaSourceRegisterer;
import dev.louis.nebula.api.entrypoint.AlternativeManaSourceRegisteringEntrypoint;
import dev.louis.nebula.api.mana.ManaSource;
import dev.louis.nebula.api.mana.factory.EntityManaSourceFactory;
import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.mana.CreativeInfiniteManaSource;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import dev.louis.nebula.util.Phase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();


    @Override
    public void onInitialize() {
        NebulaCommand.init();
        SpellEffectType.init();
        this.registerPacketReceivers();
        LOGGER.info("Nebula has been initialized.");
        AlternativeManaSourceRegisterer alternativeManaSourceRegisterer = new AlternativeManaSourceRegisterer();
        alternativeManaSourceRegisterer.registerPre(Identifier.of(MOD_ID, "creative"), CreativeInfiniteManaSource::new);

        FabricLoader.getInstance().invokeEntrypoints(
                "alternativeManaSource",
                AlternativeManaSourceRegisteringEntrypoint.class,
                (entrypoint -> entrypoint.registerAlternativeManaSources(alternativeManaSourceRegisterer))
        );

    }

    private void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
    }

    public static final HashMap<Identifier, EntityManaSourceFactory> preManaAlternativeFactories = new HashMap<>();
    public static final HashMap<Identifier, EntityManaSourceFactory> postManaAlternativeFactories = new HashMap<>();

    public static void registerManaAlternativeInPhase(Identifier id, EntityManaSourceFactory factory, Phase phase) {
        var map = mapForPhase(phase);

        var duplicateId = map.containsKey(id);
        if (duplicateId) throw new IllegalStateException("Duplicate identifier in " + phase + "! (" + id + ")");

        map.put(id, factory);
    }

    private static HashMap<Identifier, EntityManaSourceFactory> mapForPhase(Phase phase) {
        return switch (phase) {
            case PRE -> preManaAlternativeFactories;
            case POST -> postManaAlternativeFactories;
        };
    }

    public static Collection<ManaSource> createManaSourcesFor(LivingEntity entity, Phase phase) {
        return mapForPhase(phase).values().stream().map(factory -> factory.create(entity)).filter(Objects::nonNull).toList();
    }

}

