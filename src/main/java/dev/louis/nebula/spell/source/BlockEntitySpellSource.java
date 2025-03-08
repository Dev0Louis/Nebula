package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.storage.ManaStorageHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.component.CastComponent;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.component.CastComponents;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public class BlockEntitySpellSource<BE extends BlockEntity> implements SpellSource<BE> {
    private final BE blockEntity;
    private final ServerWorld world;
    private final BlockPos blockPos;
    private final Map<CastComponent<?>, Object> customDataMap;

    public BlockEntitySpellSource(BE blockEntity, ServerWorld world, BlockPos blockPos) {
        this(blockEntity, world, blockPos, new HashMap<>());
    }

    public BlockEntitySpellSource(BE blockEntity, ServerWorld world, BlockPos blockPos, Map<CastComponent<?>, Object> customDataMap) {
        this.blockEntity = blockEntity;
        this.world = world;
        this.blockPos = blockPos;
        this.customDataMap = customDataMap;
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
    public void drainThaum(long amount, TransactionContext context) throws SpellFumble {
        if (this.expectCastData(CastComponents.MANA_POOL).extractThaum(amount, context) != amount) {
            throw SpellFumble.manaFumble();
        }
    }

    @Override
    public void startSpellEffect(SpellEffect spellEffect, TransactionContext transaction) throws SpellFumble {
        throw SpellFumble.spellEffectFumble();
    }

    @Override
    public <Data> Optional<Data> getCastData(CastComponent<Data> castComponent) {
        return Optional.ofNullable((Data) this.customDataMap.get(castComponent));
    }

    @Override
    public <Data> void setCastData(CastComponent<Data> castComponent, Data data) {
        this.customDataMap.put(castComponent, data);
    }
}
