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
    public static final RegistryKey<Registry<SpellType<?>>> SPELL_REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MOD_ID, "spell_type"));
    public static final SimpleRegistry<SpellType<?>> SPELL_REGISTRY = FabricRegistryBuilder.createSimple(SPELL_REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    @Override
    public void onInitialize() {
        SpellType.init();
        this.registerPacketReceivers();
        NebulaManager.INSTANCE.init();
        NebulaCommand.init();
        LOGGER.info("Nebula has been initialized.");

    }

    public void registerPacketReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SpellCastC2SPacket.getId(), SpellCastC2SPacket::receive);
    }
}

