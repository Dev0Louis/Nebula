package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.mana.storage.ManaStorageHolder;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Optional;

@ApiStatus.Internal
public class EntitySpellSource<E extends Entity> implements SpellSource<E> {
    protected final E entity;
    private final ServerWorld world;
    private final Vec3d pos;
    private final BlockPos blockPos;

    public EntitySpellSource(E entity, ServerWorld world, Vec3d pos, BlockPos blockPos) {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
    }

    @Override
    public boolean castSpell(Spell<E> spell) {
        if (!entity.isAlive()) return false;

        return spell.tryCast(this);
    }

    @Override
    public ServerWorld getWorld() {
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
    public E getCaster() {
        return entity;
    }

    @Override
    public Optional<ManaPool> getManaPool() {
        if (entity instanceof ManaStorageHolder manaStorageHolder && manaStorageHolder.getManaStorage() instanceof ManaPool manaPool) return Optional.of(manaPool);
        return Optional.empty();
    }
}
