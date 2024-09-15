package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellException;
import dev.louis.nebula.api.spell.SpellSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class WorldSpellSource implements SpellSource<World> {
    protected final World world;
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
    public World getSource() {
        return world;
    }
}
