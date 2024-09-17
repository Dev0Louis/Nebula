package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.spell.quick.QuickSpell;
import dev.louis.nebula.api.spell.quick.QuickSpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntityQuickSpellSource<Entity extends LivingEntity> implements QuickSpellSource<Entity> {
    private final Entity entity;
    private final World world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public EntityQuickSpellSource(Entity entity, World world, Vec3d pos, BlockPos blockPos) {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
    }

    @Override
    public void castSpell(QuickSpell<Entity> quickSpell) {
        if (!entity.isAlive()) return;

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
    public Entity getCaster() {
        return entity;
    }
}
