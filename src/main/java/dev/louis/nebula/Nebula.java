package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.manager.NebulaManager;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import net.fabricmc.api.ModInitializer;
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
        ServerPlayNetworking.registerGlobalReceiver(SpellCastC2SPacket.TYPE, SpellCastC2SPacket::receive);
    }
}

