package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        SpellType.init();
        registerPacketReceivers();
        NebulaRegistries.init();
        NebulaManager.INSTANCE.init();
        NebulaCommand.init();
        LOGGER.info("Nebula has started.");
    }

    public void registerPacketReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SpellCastC2SPacket.getId(), SpellCastC2SPacket::receive);
    }



    public static class NebulaRegistries {
        public static SimpleRegistry<SpellType<?>> SPELL_TYPE = FabricRegistryBuilder.createSimple(NebulaRegistryKeys.SPELL_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();
        public static void init(){}
    }
    public static class NebulaRegistryKeys {
        public static final RegistryKey<Registry<SpellType<?>>> SPELL_TYPE = RegistryKey.ofRegistry(new Identifier("nebula","spell_type"));
    }
}

