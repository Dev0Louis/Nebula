package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebula.spell.source.BlockEntitySpellSource;
import dev.louis.nebula.spell.source.EntitySpellSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public interface SpellSource<Caster> {
    boolean castSpell(Spell<Caster> spell, Transaction transaction);

    ServerWorld getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();
    Optional<ManaPool> getManaPool();

    /**
     * Drains mana from the SpellSource if the required mana can't be supplied a {@link SpellFumble} will be thrown.
     * @param amount The amount of thaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    void drainThaum(long amount, TransactionContext context) throws SpellFumble;

    /**
     * Drains 1000 times more thaum than {@link SpellSource#drainThaum(long, TransactionContext)}. (A Kilo)
     * @param amount The amount of kilothaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    default void drainKilothaum(long amount, TransactionContext context) throws SpellFumble {
        drainThaum(amount * 1000L, context);
    }
    void startSpellEffect(SpellEffect spellEffect, TransactionContext transaction) throws SpellFumble;


    static <E extends Entity> SpellSource<E> of(ServerWorld world, E entity, Vec3d castPos) {
        return new EntitySpellSource<>(entity, world, castPos, BlockPos.ofFloored(castPos));
    }

    static <E extends Entity> SpellSource<E> of(ServerWorld world, E entity) {
        return of(world, entity, entity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(ServerWorld world, BE blockEntity) {
        return of(world, blockEntity, blockEntity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(ServerWorld world, BE blockEntity, BlockPos blockPos) {
        return new BlockEntitySpellSource<>(blockEntity, world, blockPos);
    }
}
