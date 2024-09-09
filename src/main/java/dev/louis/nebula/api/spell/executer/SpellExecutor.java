package dev.louis.nebula.api.spell.executer;

public interface SpellExecutor {
    void tick();
    void onEnable();
    void onDisable();
}
