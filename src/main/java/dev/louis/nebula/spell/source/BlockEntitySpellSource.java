package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaPoolHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BlockEntitySpellSource<BE extends BlockEntity> implements SpellSource<BE> {
    protected BE blockEntity;
    private final World world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public BlockEntitySpellSource(BE blockEntity, World world, Vec3d pos, BlockPos blockPos) {
        this.blockEntity = blockEntity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
    }

    @Override
    public void castSpell(Spell<BE> spell) {
        if (blockEntity.isRemoved()) return;

        try {
            spell.cast(this);
        } catch (SpellException e) {
            e.onFail(this);
        }
    }


    @Override
    public World getWorld() {
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
    public BE getCaster() {
        return blockEntity;
    }

    @Override
    public ManaPool getManaPool() {
        if (blockEntity instanceof ManaPoolHolder manaPoolHolder) manaPoolHolder.getManaPool();
        return ManaPool.EMPTY;
    }
}
