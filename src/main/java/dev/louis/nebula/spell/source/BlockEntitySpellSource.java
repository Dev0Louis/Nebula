package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    public boolean castSpell(Spell<BE> spell) {
        if (blockEntity.isRemoved()) return false;

        return spell.tryCast(this);
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
        if (blockEntity instanceof ManaPoolHolder manaPoolHolder) return Optional.of(manaPoolHolder.getManaPool());
        return Optional.empty();
    }
}
