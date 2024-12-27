package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.spell.source.BlockEntitySpellSource;
import dev.louis.nebula.spell.source.EntitySpellSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public interface SpellSource<Caster> {
    boolean castSpell(Spell<Caster> spell);

    ServerWorld getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();
    Optional<ManaPool> getManaPool();
    boolean drainMana(long amount, TransactionContext context);
    default boolean drainKilomana(int amount, TransactionContext context) {
        return drainMana(amount * 1000L, context);
    }


    static <E extends Entity> SpellSource<E> of(ServerWorld world, E entity, Vec3d castPos) {
        return new EntitySpellSource<>(entity, world, castPos, BlockPos.ofFloored(castPos));
    }

    static <E extends LivingEntity> SpellSource<E> of(ServerWorld world, E entity) {
        return of(world, entity, entity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(ServerWorld world, BE blockEntity) {
        return of(world, blockEntity, blockEntity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(ServerWorld world, BE blockEntity, BlockPos blockPos) {
        return new BlockEntitySpellSource<>(blockEntity, world, blockPos);
    }
}
