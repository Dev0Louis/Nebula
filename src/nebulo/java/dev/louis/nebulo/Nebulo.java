package dev.louis.nebulo;

import com.mojang.logging.LogUtils;
import dev.louis.nebulo.client.SpellCreator;
import dev.louis.nebulo.networking.CastSpellPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;

public class Nebulo implements ModInitializer {
    public static final String MOD_ID = "nebulo";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(CastSpellPayload.ID, CastSpellPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CastSpellPayload.ID, Nebulo::receiveSpellCast);

        NebuloSpellEffects.init();
        SpellCreator.init();
        LOGGER.info("Nebulo has been initialized.");
    }

    private static void receiveSpellCast(CastSpellPayload castSpellPayload, ServerPlayNetworking.Context context) {
        var spellCreator = SpellCreator.REGISTRY.get(castSpellPayload.spellId());

        context.player().castSpell(spellCreator.create(context.player()));
    }
}
