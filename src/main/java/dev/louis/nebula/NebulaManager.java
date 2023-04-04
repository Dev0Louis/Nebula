package dev.louis.nebula;

import dev.louis.nebula.knowledgemanager.NebulaSpellKnowledgeManager;
import dev.louis.nebula.knowledgemanager.SpellKnowledgeManager;
import dev.louis.nebula.knowledgemanager.player.PlayerSpellKnowledgeManager;
import dev.louis.nebula.manamanager.ManaManager;
import dev.louis.nebula.manamanager.NebulaManaManager;
import dev.louis.nebula.manamanager.player.PlayerManaManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class NebulaManager {
    private static final String JSON_KEY_CONTAINS_MANAMANAGER = "nebula:contains_manamanager";
    private static final String JSON_KEY_CONTAINS_SPELLKNOWLEDGEMANAGER = "nebula:contains_spellknowledgemanager";
    public static final NebulaManager INSTANCE = new NebulaManager();


    private boolean nebulaManaManagerActive = true;
    private boolean nebulaSpellManagerActive = true;
    private Optional<ManaManager> manaManager = Optional.empty();
    private Optional<SpellKnowledgeManager> spellKnowledgeManager = Optional.empty();


    private NebulaManager() {

    }

    public static void init() {
        INSTANCE.internal_init();
    }
    private void internal_init() {
        load();
        if(nebulaManaManagerActive) {
            registerManaManager(new NebulaManaManager());
        }
        if(nebulaSpellManagerActive) {
            registerSpellKnowledgeManager(new NebulaSpellKnowledgeManager());
        }
    }


    private void load() {
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            final ModMetadata meta = container.getMetadata();
            if(meta.getId().equals("nebula"))continue;
            if (meta.containsCustomValue(JSON_KEY_CONTAINS_MANAMANAGER)) {
                nebulaManaManagerActive = false;
            }
            if (meta.containsCustomValue(JSON_KEY_CONTAINS_SPELLKNOWLEDGEMANAGER)) {
                nebulaSpellManagerActive = false;
            }
        }
    }
    public void registerManaManager(ManaManager<?> manaManager) {
        if (manaManager == null) {
            throw new NullPointerException("Attempt to register a NULL ManaManager");
        } else if (this.manaManager.isPresent()) {
            throw new UnsupportedOperationException("A second ManaManager attempted to register. Multiple ManaManagers are not supported.");
        } else {
            this.manaManager = Optional.of(manaManager);
            manaManager.setUp();
        }
    }

    public void registerSpellKnowledgeManager(SpellKnowledgeManager<?> spellKnowledgeManager) {
        if (spellKnowledgeManager == null) {
            throw new NullPointerException("Attempt to register a NULL SpellKnowledgeManager");
        } else if (this.spellKnowledgeManager.isPresent()) {
            throw new UnsupportedOperationException("A SpellKnowledgeManager plug-in attempted to register. Multiple SpellKnowledgeManagers are not supported.");
        } else {
            this.spellKnowledgeManager = Optional.of(spellKnowledgeManager);
            spellKnowledgeManager.setUp();
        }
    }



    public ManaManager<?> getManaManager() {
        return manaManager.orElseThrow();
    }

    public SpellKnowledgeManager<?> getSpellKnowledgeManager() {
        return spellKnowledgeManager.orElseThrow();
    }

    public PlayerManaManager createPlayerManaManager(PlayerEntity player) {
        return getManaManager().createPlayerManaManager(player);
    }

    public PlayerSpellKnowledgeManager createPlayerSpellKnowledgeManager(PlayerEntity player) {
        return getSpellKnowledgeManager().createPlayerSpellKnowledgeManager(player);
    }

}
