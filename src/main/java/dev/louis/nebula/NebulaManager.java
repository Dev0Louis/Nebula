package dev.louis.nebula;

import dev.louis.nebula.api.manager.mana.ManaManager;
import dev.louis.nebula.api.manager.mana.entrypoint.RegisterManaManagerEntrypoint;
import dev.louis.nebula.api.manager.mana.registerable.ManaManagerRegistrableView;
import dev.louis.nebula.api.manager.spell.SpellManager;
import dev.louis.nebula.api.manager.spell.entrypoint.RegisterSpellManagerEntrypoint;
import dev.louis.nebula.api.manager.spell.registerable.SpellManagerRegistrableView;
import dev.louis.nebula.manager.mana.NebulaManaManager;
import dev.louis.nebula.manager.spell.NebulaSpellManager;
import dev.louis.nebula.networking.SyncManaS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class NebulaManager implements ManaManagerRegistrableView, SpellManagerRegistrableView {
    private static ModContainer manaManagerMod;
    private static ManaManager.Factory<?> manaManagerFactory;
    private static ModContainer spellManagerMod;
    private static SpellManager.Factory<?> spellManagerFactory;
    private static boolean isLocked = false;
    private static ClientPlayNetworking.PlayChannelHandler spellPlayChannelHandler;
    private static ClientPlayNetworking.PlayChannelHandler manaPlayChannelHandler;
    private static Identifier manaPacketId;
    private static Identifier spellPacketId;

    private NebulaManager() {}

    public static void init() {
        if(NebulaManager.isLocked) throw new IllegalStateException("Registration of Managers is locked!");
        NebulaManager nebulaManager = new NebulaManager();
        nebulaManager.runEntrypointsOrThrow();
        nebulaManager.lock();
        nebulaManager.printInfo();
    }

    public static ManaManager.Factory<?> getManaManagerFactory() {
        return manaManagerFactory;
    }

    public static SpellManager.Factory<?> getSpellManagerFactory() {
        return spellManagerFactory;
    }

    public static ManaManager createManaManager(PlayerEntity player) {
        return getManaManagerFactory().createPlayerManaManager(player);
    }

    public static SpellManager createSpellManager(PlayerEntity player) {
        return getSpellManagerFactory().createSpellKnowledgeManager(player);
    }

    public void lock() {
        if(spellManagerFactory == null) {
            registerSpellManager(NebulaSpellManager::new, UpdateSpellCastabilityS2CPacket.ID, NebulaSpellManager::receiveSync);
            spellManagerMod = FabricLoader.getInstance().getModContainer(Nebula.MOD_ID).orElseThrow();
        }
        if(manaManagerFactory == null) {
            registerManaManager(NebulaManaManager::new, SyncManaS2CPacket.ID, NebulaManaManager::receiveSync);
            manaManagerMod = FabricLoader.getInstance().getModContainer(Nebula.MOD_ID).orElseThrow();
        }
        isLocked = true;
    }

    @Override
    public void registerManaManager(
            ManaManager.Factory<?> manaManagerFactory,
            Identifier packetId,
            ClientPlayNetworking.PlayChannelHandler manaChannelHandler
    ) {
        NebulaManager.manaManagerFactory = manaManagerFactory;
        NebulaManager.manaPacketId = packetId;
        NebulaManager.manaPlayChannelHandler = manaChannelHandler;
    }

    @Override
    public void registerSpellManager(
            SpellManager.Factory<?> spellManagerFactory,
            Identifier packetId,
            ClientPlayNetworking.PlayChannelHandler spellChannelHandler
    ) {
        NebulaManager.spellManagerFactory = spellManagerFactory;
        NebulaManager.spellPacketId = packetId;
        NebulaManager.spellPlayChannelHandler = spellChannelHandler;
    }

    /**
     * Throws an exception if multiple mods want to override the ManaManager or SpellManager.
     */
    private void runEntrypointsOrThrow() {
        var manaManagerEntrypoints = FabricLoader.getInstance().getEntrypointContainers("registerManaManager", RegisterManaManagerEntrypoint.class)
                .stream().filter(container -> container.getEntrypoint().shouldRegister()).toList();

        var spellManagerEntrypoints = FabricLoader.getInstance().getEntrypointContainers("registerSpellManager", RegisterSpellManagerEntrypoint.class)
                .stream().filter(container -> container.getEntrypoint().shouldRegister()).toList();

        var manaManagerMods = manaManagerEntrypoints.stream().map(EntrypointContainer::getProvider).toList();
        var spellManagerMods = spellManagerEntrypoints.stream().map(EntrypointContainer::getProvider).toList();

        if(manaManagerEntrypoints.size() > 1 && spellManagerEntrypoints.size() > 1) {
            throw new IllegalStateException("Multiple Mods want to override the ManaManager and SpellManager!\n " +
                    "Mods overriding ManaManager: " + manaManagerMods + ",\n" +
                    "Mods overriding SpellManager: " + spellManagerMods);
        }

        if(manaManagerEntrypoints.size() > 1) {
            throw new IllegalStateException("Multiple Mods want to override the ManaManager! Mods: " + manaManagerMods);
        }

        if(spellManagerEntrypoints.size() > 1) {
            throw new IllegalStateException("Multiple Mods want to override the SpellManager! Mods: " + spellManagerMods);
        }

        runEntryPoints(getFirstOrNull(manaManagerEntrypoints), getFirstOrNull(spellManagerEntrypoints));
    }

    private void runEntryPoints(
            EntrypointContainer<RegisterManaManagerEntrypoint> manaManagerEntrypointEntrypointContainer,
            EntrypointContainer<RegisterSpellManagerEntrypoint> spellManagerEntrypointEntrypointContainer
    ) {
        if(manaManagerEntrypointEntrypointContainer != null) {
            manaManagerEntrypointEntrypointContainer.getEntrypoint().registerSpell(this);
            NebulaManager.manaManagerMod = manaManagerEntrypointEntrypointContainer.getProvider();
        }

        if(spellManagerEntrypointEntrypointContainer != null) {
            spellManagerEntrypointEntrypointContainer.getEntrypoint().registerSpell(this);
            NebulaManager.spellManagerMod = spellManagerEntrypointEntrypointContainer.getProvider();
        }
    }

    private void printInfo() {
        Nebula.LOGGER.info("ManaManager is registered by: " + NebulaManager.manaManagerMod.getMetadata().getName());
        Nebula.LOGGER.info("SpellManager is registered by: " + NebulaManager.spellManagerMod.getMetadata().getName());
    }

    private static <T> T getFirstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    @Environment(EnvType.CLIENT)
    public static void registerPacketReceivers() {
        if(!NebulaManager.isLocked) {
            throw new IllegalStateException("NebulaManager is not locked yet!");
        }
        ClientPlayNetworking.registerGlobalReceiver(NebulaManager.manaPacketId, NebulaManager.manaPlayChannelHandler);
        Nebula.LOGGER.info("Registered ManaManager Packet Receiver with id: " + NebulaManager.manaPacketId);
        ClientPlayNetworking.registerGlobalReceiver(NebulaManager.spellPacketId, NebulaManager.spellPlayChannelHandler);
        Nebula.LOGGER.info("Registered SpellManager Packet Receiver with id: " + NebulaManager.spellPacketId);
    }
}
