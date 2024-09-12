package dev.louis.nebula.api.spell.executor;

import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class SpellExecutor {
    private final SpellExecutorType<?> type;

    protected SpellExecutor(SpellExecutorType<?> type) {
        this.type = type;
    }

    public abstract void tick(World world);
    public abstract boolean shouldContinue(World world);
    public abstract void onEnable(World world);
    public abstract void onDisable(World world);

    public SpellExecutorType<?> getType() {
        return type;
    }
}
