package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.storage.ManaStorageHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebula.spell.SpellCastHelper;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class BlockEntitySpellSource<BE extends BlockEntity> implements SpellSource<BE> {
    private final BE blockEntity;
    private final ServerWorld world;
    private final BlockPos blockPos;

    public BlockEntitySpellSource(BE blockEntity, ServerWorld world, BlockPos blockPos) {
        this.blockEntity = blockEntity;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public boolean castSpell(Spell<BE> spell, Transaction transaction) {
        if (blockEntity.isRemoved()) return false;

        return SpellCastHelper.tryCast(this, spell, transaction);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }

    @Override
    public Vec3d getPos() {
        return blockPos.toCenterPos();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public BE getCaster() {
        return blockEntity;
    }

    @Override
    public Optional<ManaPool> getManaPool() {
        if (blockEntity instanceof ManaStorageHolder manaStorageHolder && manaStorageHolder.getManaStorage() instanceof ManaPool manaPool) return Optional.of(manaPool);
        return Optional.empty();
    }

    @Override
    public void drainMana(long amount, TransactionContext context) throws SpellFumble {
        getManaPool().map(manaPool -> (manaPool.extractMana(amount, context) == amount)).filter(Boolean::booleanValue).orElseThrow(SpellFumble::new);
    }

    @Override
    public void startSpellEffect(SpellEffect spellEffect, TransactionContext transaction) throws SpellFumble {
        throw new SpellFumble();
    }
}
