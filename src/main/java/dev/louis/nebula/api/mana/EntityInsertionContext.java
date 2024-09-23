package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.NebulaEntityInsertionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.NonExtendable
public interface EntityInsertionContext {
    /**
     * The entity that capacity is being inserted into.
     */
    LivingEntity entity();

    /**
     * The player that capacity is being inserted into.
     */
    default Optional<PlayerEntity> player() {
        return Optional.of(entity()).filter(PlayerEntity.class::isInstance).map(PlayerEntity.class::cast);
    }

    /**
     * The world that the insertion is taking place in.
     */
    World world();

    /**
     * The position at which the capacity is inserted into.
     * For example the pos of an entity or a block in which capacity is inserted into.
     */
    Vec3d pos();

    /**
     * The BlockPos at which the capacity is inserted into.
     * For example the BlockPos of an entity or a block in which capacity is inserted into.
     */
    BlockPos blockPos();

    /**
     * The amount that is currently present.
     */
    float amount();

    /**
     * The amount that is inserted.
     */
    float insertion();

    /**
     * The amount that was requested to be inserted.
     */
    float requestedInsertion();

    static EntityInsertionContext create(
            LivingEntity entity,
            float amount,
            float insertion,
            float requestedInsertion
    ) {
        return new NebulaEntityInsertionContext(
                entity,
                entity.getWorld(),
                entity.getPos(),
                entity.getBlockPos(),
                amount,
                insertion,
                requestedInsertion
        );
    }
}
