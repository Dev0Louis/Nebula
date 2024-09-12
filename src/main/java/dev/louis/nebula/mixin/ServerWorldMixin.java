package dev.louis.nebula.mixin;

import dev.louis.nebula.api.spell.executor.SpellExecutor;
import dev.louis.nebula.api.spell.executor.SpellExecutorExecutor;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements SpellExecutorExecutor {
    @Unique
    protected List<SpellExecutor> spellExecutors = new ArrayList<>();

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }


    @Inject(method = "tick", at = @At("RETURN"))
    public void tickSpells(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        stopSpellExecutorIf(executor -> !executor.shouldContinue(this));
        for (SpellExecutor spellExecutor : spellExecutors) {
            spellExecutor.tick(this);
        }
    }

    @Override
    public boolean startSpellExecutor(SpellExecutor executor) {
        executor.onEnable(this);
        return spellExecutors.add(executor);
    }

    @Override
    public boolean stopSpellExecutorIf(Predicate<SpellExecutor> stopper) {
        return spellExecutors.removeIf(executor -> {
            boolean remove = stopper.test(executor);
            if (remove) executor.onDisable(this);
            return remove;
        });
    }

    @Override
    public Stream<SpellExecutor> streamSpellExecutors() {
        return this.spellExecutors.stream();
    }
}
