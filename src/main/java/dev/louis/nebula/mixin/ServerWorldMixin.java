package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.world.SpellWorld;
import dev.louis.nebula.spell.SpellList;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
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

//Very good code, rai, auri and arko approved :)
// JACG is cool.
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements SpellWorld {

    @Shadow public abstract TickManager getTickManager();

    @Shadow @Final private ServerChunkManager chunkManager;
    @Unique
    final SpellList spellList = new SpellList();

    @Override
    public boolean startSpell(Spell spell) {
        if (spellList.has(spell)) return false;
        spellList.add(spell);
        return true;
    }

    @Override
    public boolean stopSpell(int id) {
        if (!spellList.has(id)) return false;
        spellList.remove(id);
        return true;
    }

    @Override
    public Spell getSpell(int id) {
        return spellList.get(id);
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V", shift = At.Shift.AFTER)
    )
    public void tickSpells(BooleanSupplier shouldKeepTicking, CallbackInfo ci, @Local TickManager tickManager, @Local Profiler profiler) {
        spellList.forEach(spell -> {
            if (spell.hasEnded()) return;
            if (tickManager.shouldTick()) {
                profiler.push("checkDespawn");
                spell.checkEnded();
                profiler.pop();
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
        //TODO: Implement proper profiling
        //this.getProfiler().push(() -> Registries.ENTITY_TYPE.getId(entity.getType()).toString());
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
