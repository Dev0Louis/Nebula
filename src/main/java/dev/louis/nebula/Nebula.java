package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        NebulaCommand.init();
        this.registerPacketReceivers();
        LOGGER.info("Nebula has been initialized.");
    }

    public void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
    }
}

