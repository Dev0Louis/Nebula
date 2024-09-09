package dev.louis.nebula.api.spell.executer;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SpellExecutor {
    void tick();
    void onEnable();
    void onDisable();
}
