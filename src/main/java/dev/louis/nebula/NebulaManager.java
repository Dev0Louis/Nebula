package dev.louis.nebula;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.mana.manager.NebulaManaManager;
import dev.louis.nebula.spell.manager.NebulaSpellManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.player.PlayerEntity;

public class NebulaManager {
    private static final String JSON_KEY_CONTAINS_MANA_MANAGER = "nebula:contains_mana_manager";
    private static final String JSON_KEY_CONTAINS_SPELL_MANAGER = "nebula:contains_spell_manager";
    public static final NebulaManager INSTANCE = new NebulaManager();
    private NebulaManager(){}

    private ManaManager.Factory<?> manaManagerFactory;
    private SpellManager.Factory<?> spellManagerFactory;
    private boolean loadManaManager = true;
    private boolean loadSpellManager = true;
    public void init() {
        load();
        if(loadManaManager)registerManaManagerFactory(NebulaManaManager::new);
        if(loadSpellManager)registerSpellManagerFactory(NebulaSpellManager::new);
    }
    private void load() {
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            final ModMetadata metadata = mod.getMetadata();
            if(metadata.getId().equals("nebula"))return;
            var loadManaManager = metadata.getCustomValue(JSON_KEY_CONTAINS_MANA_MANAGER);
            if (loadManaManager != null && loadManaManager.getAsBoolean()) this.loadManaManager = false;
            var loadSpellManager = metadata.getCustomValue(JSON_KEY_CONTAINS_SPELL_MANAGER);
            if (loadSpellManager != null && loadSpellManager.getAsBoolean()) this.loadSpellManager = false;
        });
    }

    public void registerManaManagerFactory(ManaManager.Factory<?> manaManager) {
        if (manaManager == null) {
            throw new NullPointerException("Attempt to register a NULL ManaManager");
        }
        if (this.manaManagerFactory != null) {
            throw new UnsupportedOperationException("A second ManaManager attempted to register. Multiple ManaManagers are not supported.");
        }
        this.manaManagerFactory = manaManager;
    }

    public void registerSpellManagerFactory(SpellManager.Factory<?> spellKnowledgeManager) {
        if (spellKnowledgeManager == null) {
            throw new NullPointerException("Attempt to register a NULL SpellKnowledgeManager");
        }
        if (this.spellManagerFactory != null) {
            throw new UnsupportedOperationException("A SpellKnowledgeManager plug-in attempted to register. Multiple SpellKnowledgeManagers are not supported.");
        }
        this.spellManagerFactory = spellKnowledgeManager;
    }

    public ManaManager.Factory<?> getManaManagerFactory() {
        return manaManagerFactory;
    }
    public SpellManager.Factory<?> getSpellManagerFactory() {
        return spellManagerFactory;
    }
    public static ManaManager createManaManager(PlayerEntity player) {
        return INSTANCE.getManaManagerFactory().createPlayerManaManager(player);
    }
    public static SpellManager createSpellManager(PlayerEntity player) {
        return INSTANCE.getSpellManagerFactory().createSpellKnowledgeManager(player);
    }
}
