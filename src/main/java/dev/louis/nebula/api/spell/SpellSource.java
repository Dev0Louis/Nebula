package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.api.spell.component.CastComponent;
import dev.louis.nebula.api.spell.component.CastComponents;
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
    boolean tryCastSpell(Spell<Caster> spell, Transaction transaction);

    ServerWorld getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();

    default ManaSource expectManaSource() throws SpellFumble {
        return this.expectComponent(CastComponents.MANA_SOURCE);
    }

    /**
     * Drains mana from the SpellSource if the required mana can't be supplied a {@link SpellFumble} will be thrown.
     * @param amount The amount of thaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    void expectThaum(long amount, TransactionContext context) throws SpellFumble;

    /**
     * Drains 1000 times more thaum than {@link SpellSource#expectThaum(long, TransactionContext)}. (A Kilo)
     * @param amount The amount of kilothaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    default void expectKilothaum(long amount, TransactionContext context) throws SpellFumble {
        expectThaum(amount * 1000L, context);
    }

    /**
     * Drains mana from the SpellSource if the required mana can't be supplied a {@link SpellFumble} will be thrown.
     * @param amount The amount of thaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    default void expectThaum(Number amount, TransactionContext context) throws SpellFumble {
        expectThaum(amount.longValue(), context);
    }

    /**
     * Drains 1000 times more thaum than {@link SpellSource#expectThaum(long, TransactionContext)}. (A Kilo)
     * @param amount The amount of kilothaum to drain.
     * @throws SpellFumble Thrown if mana resources are insufficient.
     */
    default void expectKilothaum(Number amount, TransactionContext context) throws SpellFumble {
        expectKilothaum(amount.longValue(), context);
    }

    void expectSpellEffectStart(SpellEffect spellEffect, TransactionContext transaction) throws SpellFumble;

    default <Value> Value expectComponent(CastComponent<Value> component) throws SpellFumble {
        return this.getComponent(component).orElseThrow(SpellFumble::new);
    }

    <Value> Optional<Value> getComponent(CastComponent<Value> component);

    <Value> void setComponent(CastComponent<Value> component, Value value);

    <Value> void removeComponent(CastComponent<Value> component);


    static <E extends Entity> SpellSource<E> of(ServerWorld world, E entity, Vec3d castPos) {
        return new EntitySpellSource<>(entity, world, castPos);
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
