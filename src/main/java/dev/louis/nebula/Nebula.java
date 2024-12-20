package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.entrypoint.AlternativeManaSourceRegisteringEntrypoint;
import dev.louis.nebula.api.spell.effect.SpellEffects;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.entrypoint.AlternativeManaSourceRegistererImpl;
import dev.louis.nebula.mana.CreativeInfiniteManaSource;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
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
        AlternativeManaSourceRegistererImpl alternativeManaSourceRegisterer = AlternativeManaSourceRegistererImpl.INSTANCE;
        alternativeManaSourceRegisterer.registerPre(Identifier.of(MOD_ID, "creative"), CreativeInfiniteManaSource::new);

        FabricLoader.getInstance().invokeEntrypoints(
                "alternativeManaSource",
                AlternativeManaSourceRegisteringEntrypoint.class,
                (entrypoint -> entrypoint.registerAlternativeManaSources(alternativeManaSourceRegisterer))
        );

        ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, keepEquipment) ->
                InternalManaManagerHolder.getManaManager(previous).copyFrom(InternalManaManagerHolder.getManaManager(converted))
        );
    }

    private void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StartSpellEffectPayload.ID, StartSpellEffectPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StopSpellEffectPayload.ID, StopSpellEffectPayload.CODEC);
    }
}

