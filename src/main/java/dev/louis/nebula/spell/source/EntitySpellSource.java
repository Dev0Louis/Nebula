package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntitySpellSource<Entity extends LivingEntity> implements SpellSource<Entity> {
    private final Entity entity;
    private final World world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public EntitySpellSource(Entity entity, World world, Vec3d pos, BlockPos blockPos) {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
    }

    @Override
    public void castSpell(Spell<Entity> spell) {
        if (!entity.isAlive()) return;

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
    public Entity getCaster() {
        return entity;
    }

    @Override
    public ManaPool getManaPool() {
        return entity.getManaPool();
    }
}
