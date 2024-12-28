package dev.louis.nebula.api.spell.effect.transaction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Experimental
public class SpellEffectWrapper extends SnapshotParticipant<SpellEffectWrapper.Data> {
    private final ServerWorld world;
    public final LivingEntity player;
    private List<Modification> spellEffectModifications = new ArrayList<>(1);

    public SpellEffectWrapper(ServerWorld world, LivingEntity player) {
        this.world = world;
        this.player = player;
    }

    public boolean startSpellEffect(SpellEffect spellEffect, TransactionContext context) {
        if (player.canStartSpellEffect(world, spellEffect)) {
            updateSnapshots(context);
            player.nebula$getSpellEffectsInternal().put(spellEffect, 0);
            spellEffectModifications.add(new Modification(spellEffect, true));
            return true;
        }
        return false;
    }

    public boolean stopSpellEffect(SpellEffect effect, TransactionContext context) {
        if (player.getSpellEffects().contains(effect)) {
            updateSnapshots(context);
            player.nebula$getSpellEffectsInternal().remove(effect);
            spellEffectModifications.add(new Modification(effect, false));
            return true;
        }
        return false;
    }

    @Override
    protected void onFinalCommit() {
        for (Modification modification : spellEffectModifications) {
            if (modification.added) {
                player.nebula$onSpellEffectStartInternal(modification.spellEffect());
            } else {
                player.nebula$onSpellEffectStoppedInternal(modification.spellEffect());
            }
        }
    }

    @Override
    protected Data createSnapshot() {
        return new Data(ImmutableMap.copyOf(player.nebula$getSpellEffectsInternal()), ImmutableList.copyOf(spellEffectModifications));
    }

    @Override
    protected void readSnapshot(Data data) {
        System.out.println("Read: " + data);

        player.nebula$setSpellEffectsInternal(new HashMap<>(data.spellEffects));
        spellEffectModifications = data.spellEffectModifications;
    }

    public record Data(Map<SpellEffect, Integer> spellEffects, List<Modification> spellEffectModifications) {

    }


    record Modification(SpellEffect spellEffect, boolean added /*true = spell was added, false = spell was removed*/) {

    }
}
