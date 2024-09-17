package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.spell.quick.QuickSpell;
import dev.louis.nebula.api.spell.quick.QuickSpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class WorldQuickSpellSource implements QuickSpellSource<World> {
    protected final World world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public WorldQuickSpellSource(World world, Vec3d pos) {
        this.world = world;
        this.pos = pos;
        this.blockPos = BlockPos.ofFloored(pos);
    }

    @Override
    public void castSpell(QuickSpell<World> quickSpell) {
        try {
            quickSpell.cast(this);
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
    public World getCaster() {
        return world;
    }
}
