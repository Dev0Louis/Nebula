package dev.louis.nebula;

import dev.louis.nebula.event.SpellCastCallback;
import dev.louis.nebula.manamanager.ManaManager;
import dev.louis.nebula.manamanager.NebulaManaManager;
import dev.louis.nebula.manamanager.player.PlayerManaManager;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nebula implements ModInitializer {
    private static final String JSON_KEY_DISABLE_NEBULAMANAMANAGER = "nebula:contains_manamanager";
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LoggerFactory.getLogger("Nebula");

    public static Nebula INSTANCE;

    private ManaManager<?> activeManaManager;
   private  boolean hasActiveManaManager = false;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        SpellType.init();
        registerPacketReceivers();
        NebulaRegistries.init();
        loadIfNeeded();
        if(nebulaManaManagerActive) {
            registerManaManager(new NebulaManaManager());
        }
    }

    public void registerPacketReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SpellCastC2SPacket.ID, ((server, player, handler, buf, responseSender) -> {
            Spell spell = SpellCastC2SPacket.read(player, buf).spell();
            if (SpellCastCallback.EVENT.invoker().interact(player, spell) == ActionResult.PASS) {
                spell.cast();
            }
        }));
    }
    private static boolean needsLoad = true;
    private static boolean nebulaManaManagerActive = true;

    private static void loadIfNeeded() {
        if (needsLoad) {
            for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
                final ModMetadata meta = container.getMetadata();
                if(meta.getId().equals("nebula"))continue;
                if (meta.containsCustomValue(JSON_KEY_DISABLE_NEBULAMANAMANAGER)) {
                    nebulaManaManagerActive = false;
                }
            }

            needsLoad = false;
        }
    }
    public void registerManaManager(ManaManager<?> manaManager) {
        if (manaManager == null) {
            throw new NullPointerException("Attempt to register a NULL rendering plug-in.");
        } else if (activeManaManager != null) {
            throw new UnsupportedOperationException("A second rendering plug-in attempted to register. Multiple rendering plug-ins are not supported.");
        } else {
            manaManager.setUp();
            this.activeManaManager = manaManager;
            this.hasActiveManaManager = true;
        }
    }

    public ManaManager<?> getManaManager() {
        return activeManaManager;
    }

    public boolean hasManaManager() {
        return hasActiveManaManager;
    }

    public PlayerManaManager createManaManager(PlayerEntity player) {
        return getManaManager().createPlayerManaManager(player);
    }



    public static class NebulaRegistries {
        public static SimpleRegistry<SpellType<? extends Spell>> SPELL_TYPE = FabricRegistryBuilder.createSimple(NebulaRegistryKeys.SPELL_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();
        public static void init(){}
    }
    public static class NebulaRegistryKeys {
        public static final RegistryKey<Registry<SpellType<? extends Spell>>> SPELL_TYPE = RegistryKeys.of("spell_type");
    }
}

