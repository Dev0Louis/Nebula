package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.manager.NebulaManager;
import dev.louis.nebula.networking.c2s.SpellCastPayload;
import dev.louis.nebula.networking.s2c.SyncManaPayload;
import dev.louis.nebula.networking.s2c.UpdateSpellCastabilityPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        SpellType.init();
        NebulaManager.init();
        NebulaCommand.init();
        this.registerPacketReceivers();
        LOGGER.info("Nebula has been initialized.");
    }

    public void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(UpdateSpellCastabilityPayload.ID, UpdateSpellCastabilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SpellCastPayload.ID, SpellCastPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SpellCastPayload.ID, SpellCastPayload::receive);
    }
}

