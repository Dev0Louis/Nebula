package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.api.world.SpellWorld;
import dev.louis.nebula.cca.NebulaCCA;
import dev.louis.nebula.world.spell.SpellList;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

//Very good code, rai, auri and arko approved :)
// JACG is cool.
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements SpellWorld {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Shadow @Final private ServerChunkManager chunkManager;

    @Shadow public abstract ChunkManager getChunkManager();

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Unique
    final SpellList spellList = new SpellList();
    @Unique
    final Int2ObjectMap<ChunkPos> spellToChunk = new Int2ObjectLinkedOpenHashMap<>();

    @Override
    public boolean startSpell(Chunk chunk, Spell spell) {
        if (spellList.has(spell)) return false;
        spellList.add(spell);
        spellToChunk.put(spell.getId(), chunk.getPos());

        return true;
    }

    @Override
    public boolean stopSpell(int id) {
        if (!spellList.has(id)) return false;
        spellList.remove(id);
        spellToChunk.remove(id);

        return true;
    }

    @Override
    public Spell getSpell(int id) {
        return spellList.get(id);
    }

    @Unique
    public void moveSpellToChunk(Spell spell, ChunkPos oldPos) {
        ChunkPos newPos = spell.getChunkPos();
        var takenSpell = NebulaCCA.SPELLS.get(this.getChunk(oldPos.x, oldPos.z)).takeSpell(spell.getId());
        NebulaCCA.SPELLS.get(this.getChunk(newPos.x, newPos.z)).giveSpell(takenSpell);

        spellToChunk.put(spell.getId(), newPos);
    };

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V", shift = At.Shift.AFTER)
    )
    public void tickSpells(BooleanSupplier shouldKeepTicking, CallbackInfo ci, @Local TickManager tickManager, @Local Profiler profiler) {
        spellList.forEach(spell -> {
            if (spell.hasEnded()) return;

            var storedPos = spellToChunk.get(spell.getId());
            if (!spell.getChunkPos().equals(storedPos)) {
                moveSpellToChunk(spell, storedPos);
            }

            if (tickManager.shouldTick()) {
                profiler.push("checkEnded");
                spell.checkEnded();
                profiler.pop();
                //
                if (this.chunkManager.chunkLoadingManager.getTicketManager().shouldTickEntities(spell.getChunkPos().toLong())) {
                    profiler.push("tick");
                    this.tickSpellOrThrow(this::tickSpell, spell);
                    profiler.pop();
                }
            }
        });
    }

    @Unique
    public void tickSpell(Spell spell) {
        ++spell.age;
        this.getProfiler().push(() -> SpellType.REGISTRY.getId(spell.getType()).toString());
        spell.tick();

    }

    @Unique
    public <T extends Spell> void tickSpellOrThrow(Consumer<T> tickConsumer, T spell) {
        try {
            tickConsumer.accept(spell);
        } catch (Throwable t) {
            CrashReport crashReport = CrashReport.create(t, "Ticking Spell (" + spell.getType() + ")");
            throw new CrashException(crashReport);
        }
    }


}
