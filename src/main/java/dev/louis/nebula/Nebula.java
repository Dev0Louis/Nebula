package dev.louis.nebula;

import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LoggerFactory.getLogger("Nebula");

    public static Nebula INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
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
        public static SimpleRegistry<SpellType<? extends Spell>> SPELL_TYPE = FabricRegistryBuilder.createSimple(NebulaRegistryKeys.SPELL_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();
        public static void init(){}
    }
    public static class NebulaRegistryKeys {
        public static final RegistryKey<Registry<SpellType<? extends Spell>>> SPELL_TYPE = RegistryKeys.of("spell_type");
    }
}

