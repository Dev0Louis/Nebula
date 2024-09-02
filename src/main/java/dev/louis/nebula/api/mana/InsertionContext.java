package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.NebulaInsertionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public interface InsertionContext {
    /**
     * The entity that mana is being inserted into.
     */
    Optional<LivingEntity> entity();

    /**
     * The player that mana is being inserted into.
     */
    default Optional<PlayerEntity> player() {
        return entity().filter(PlayerEntity.class::isInstance).map(PlayerEntity.class::cast);
    }

    /**
     * The world that the insertion is taking place in.
     */
    World world();

    /**
     * The position at which the mana is inserted into.
     * For example the pos of an entity or a block in which mana is inserted into.
     */
    Vec3d pos();

    /**
     * The BlockPos at which the mana is inserted into.
     * For example the BlockPos of an entity or a block in which mana is inserted into.
     */
    BlockPos blockPos();

    /**
     * The amount that is currently present.
     */
    float amount();

    /**
     * The amount that should be inserted.
     */
    float insertion();

    /**
     * The amount that was requested to be inserted.
     */
    float requestedInsertion();

    static InsertionContext create(
            World world,
            Vec3d pos,
            BlockPos blockPos,
            float amount,
            float insertion,
            float requestedInsertion
    ) {
        return new NebulaInsertionContext(
                Optional.empty(),
                world,
                pos,
                blockPos,
                amount,
                insertion,
                requestedInsertion
        );
    }

    static InsertionContext create(
            LivingEntity entity,
            float amount,
            float insertion,
            float requestedInsertion
    ) {
        return new NebulaInsertionContext(
                Optional.of(entity),
                entity.getWorld(),
                entity.getPos(),
                entity.getBlockPos(),
                amount,
                insertion,
                requestedInsertion
        );
    }
}
