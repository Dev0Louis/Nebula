package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.storage.ManaStorageHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.transaction.SpellEffectWrapper;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebula.spell.SpellCastHelper;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

@ApiStatus.Internal
public class EntitySpellSource<E extends Entity> implements SpellSource<E> {
    protected final E entity;
    private final ServerWorld world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public EntitySpellSource(E entity, ServerWorld world, Vec3d pos, BlockPos blockPos) {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
    }

    @Override
    public boolean castSpell(Spell<E> spell, Transaction transaction) {
        if (!entity.isAlive()) return false;

        return SpellCastHelper.tryCast(this, spell, transaction);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }

    @Override
    public Vec3d getPos() {
        return pos;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public E getCaster() {
        return entity;
    }

    @Override
    public Optional<ManaPool> getManaPool() {
        if (entity instanceof ManaStorageHolder manaStorageHolder && manaStorageHolder.getManaStorage() instanceof ManaPool manaPool) return Optional.of(manaPool);
        return Optional.empty();
    }

    @Override
    public void drainMana(long amount, TransactionContext context) throws SpellFumble {
        getManaPool().map(manaPool -> (manaPool.extractMana(amount, context) == amount)).filter(Boolean::booleanValue).orElseThrow(SpellFumble::new);
    }

    @Contract
    @Override
    public void startSpellEffect(SpellEffect spellEffect, TransactionContext context) throws SpellFumble {
        if (this.getCaster() instanceof LivingEntity livingEntity) {
            var storage = new SpellEffectWrapper(this.getWorld(), livingEntity);
            if (!storage.startSpellEffect(spellEffect, context)) throw new SpellFumble();
        }
    }
}
