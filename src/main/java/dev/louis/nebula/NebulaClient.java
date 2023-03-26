package dev.louis.nebula;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.config.NebulaConfig;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.SynchronizeSpellKnowledgeS2CPacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NebulaClient implements ClientModInitializer {
    public static NebulaConfig config;
    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
        registerAutoConfigIfInstalled();
    }

    private void registerPacketReceivers() {
        //Register the Knowledge Packet.
        ClientPlayNetworking.registerGlobalReceiver(SynchronizeSpellKnowledgeS2CPacket.ID, (client, handler, buf, responseSender) -> {
            SynchronizeSpellKnowledgeS2CPacket packet = SynchronizeSpellKnowledgeS2CPacket.read(buf);
            client.executeSync(() -> NebulaPlayer.access(client.player).getSpellKnowledge().updateCastableSpell(packet.spells()));
        });
        //Register the ManaAmount Packet.
        ClientPlayNetworking.registerGlobalReceiver(SynchronizeManaAmountS2CPacket.ID, ((client, handler, buf, responseSender) -> {
            SynchronizeManaAmountS2CPacket packet = SynchronizeManaAmountS2CPacket.read(buf);
            NebulaPlayer.access(client.player).setMana(packet.mana());
        }));
    }

    private void registerAutoConfigIfInstalled() {
        try {
            AutoConfig.register(NebulaConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(NebulaConfig.class).getConfig();
        } catch (Exception exception) {
            Nebula.LOGGER.debug("Couldn't register Cloth Config, if Cloth Config is installed this should not happen.");
        }
    }
}
