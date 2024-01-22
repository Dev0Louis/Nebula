package dev.louis.nebulo;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Nebulo implements ModInitializer {
    public static final String MOD_ID = "nebulo";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        NebuloSpells.init();
        LOGGER.info("Nebulo has been initialized.");
    }
}
