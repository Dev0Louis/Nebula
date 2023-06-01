package dev.louis.nebula;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.mana.manager.NebulaManaManager;
import dev.louis.nebula.spell.manager.NebulaSpellManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.player.PlayerEntity;

public class NebulaManager {
    private static final String JSON_KEY_CONTAINS_MANAMANAGER = "nebula:contains_manamanager";
    private static final String JSON_KEY_CONTAINS_SPELLMANAGER = "nebula:contains_spellmanager";
    public static final NebulaManager INSTANCE = new NebulaManager();
    private NebulaManager(){}

    private ManaManager.Factory<?> manaManager;
    private SpellManager.Factory<?> spellKnowledgeManager;
    private boolean nebulaManaManagerActive = true;
    private boolean nebulaSpellManagerActive = true;
    public void init() {
        load();
        if(nebulaManaManagerActive)registerManaManagerFactory(NebulaManaManager::new);
        if(nebulaSpellManagerActive)registerSpellManagerFactory(NebulaSpellManager::new);
    }
    private void load() {
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            final ModMetadata meta = container.getMetadata();
            if(meta.getId().equals("nebula"))continue;
            if (meta.containsCustomValue(JSON_KEY_CONTAINS_MANAMANAGER)) nebulaManaManagerActive = false;
            if (meta.containsCustomValue(JSON_KEY_CONTAINS_SPELLMANAGER)) nebulaSpellManagerActive = false;
        }
    }

    public void registerManaManagerFactory(ManaManager.Factory<?> manaManager) {
        if (manaManager == null) {
            throw new NullPointerException("Attempt to register a NULL ManaManager");
        }
        if (this.manaManager != null) {
            throw new UnsupportedOperationException("A second ManaManager attempted to register. Multiple ManaManagers are not supported.");
        }
        this.manaManager = manaManager;
    }

    public void registerSpellManagerFactory(SpellManager.Factory<?> spellKnowledgeManager) {
        if (spellKnowledgeManager == null) {
            throw new NullPointerException("Attempt to register a NULL SpellKnowledgeManager");
        }
        if (this.spellKnowledgeManager != null) {
            throw new UnsupportedOperationException("A SpellKnowledgeManager plug-in attempted to register. Multiple SpellKnowledgeManagers are not supported.");
        }
        this.spellKnowledgeManager = spellKnowledgeManager;
    }

    public ManaManager.Factory<?> getManaManager() {
        return manaManager;
    }
    public SpellManager.Factory<?> getSpellManager() {
        return spellKnowledgeManager;
    }
    public static ManaManager createManaManager(PlayerEntity player) {
        return INSTANCE.getManaManager().createPlayerManaManager(player);
    }
    public static SpellManager createSpellManager(PlayerEntity player) {
        return INSTANCE.getSpellManager().createPlayerSpellKnowledgeManager(player);
    }
}
