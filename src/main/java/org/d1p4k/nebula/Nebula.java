package org.d1p4k.nebula;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.d1p4k.nebula.api.NebulaSpellRegisterEntrypoint;
import org.d1p4k.nebula.commands.NebluaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nebula implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Nebula");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NebluaCommand.register(dispatcher);
        });
        FabricLoader.getInstance().getEntrypointContainers("nebula", NebulaSpellRegisterEntrypoint.class).forEach(entrypoint -> {
            entrypoint.getEntrypoint().registerSpells();
        });
    }
}

