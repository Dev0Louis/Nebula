package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.event.SpellCastEvent;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

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
    public boolean castSpell(Spell<BE> spell) {
        var allowed = !blockEntity.isRemoved() && SpellCastEvent.BEFORE.invoker().allowSpellCast(this, spell);
        if (!allowed) return false;

        try {
            spell.cast(this);
        } catch (SpellException e) {
            return false;
        }

        SpellCastEvent.AFTER.invoker().onSpellCast(this, spell);
        return true;
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
    public Optional<ManaPool> getManaPool() {
        if (blockEntity instanceof ManaPoolHolder manaPoolHolder) return Optional.of(manaPoolHolder.getManaPool());
        return Optional.empty();
    }
}
