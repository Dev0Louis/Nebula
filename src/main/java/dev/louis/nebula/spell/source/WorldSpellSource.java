package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellException;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.executor.SpellExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WorldSpellSource implements SpellSource<World> {
    private final World world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public WorldSpellSource(World world, Vec3d pos) {
        this.world = world;
        this.pos = pos;
        this.blockPos = BlockPos.ofFloored(pos);
    }

    @Override
    public void castSpell(Spell<World> spell) {
        try {
            spell.cast(this);
        } catch (SpellException e) {
            e.onFail(this);
        }
    }

    @Override
    public boolean startExecutor(SpellExecutor spellExecutor) {
        return world.startSpellExecutor(spellExecutor);
    }

    @Override
    public boolean isActive() {
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
    public ManaPool getManaPool() {
        return ManaPool.EMPTY;
    }

    @Override
    public World getSource() {
        return world;
    }
}
