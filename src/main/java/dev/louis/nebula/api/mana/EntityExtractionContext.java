package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.NebulaEntityExtractionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.NonExtendable
public interface EntityExtractionContext {
        /**
         * The entity that capacity is being extracted from.
         */
        LivingEntity entity();

        /**
         * The player that capacity is being extracted from.
         */
        default Optional<PlayerEntity> player() {
            return Optional.of(entity()).filter(PlayerEntity.class::isInstance).map(PlayerEntity.class::cast);
        }

        /**
         * The world that the extraction is taking place in.
         */
        World world();

        /**
         * The position at which the capacity is extracted from.
         * For example the pos of an entity or a block from which capacity is extracted.
         */
        Vec3d pos();

        /**
         * The BlockPos at which the capacity is extracted from.
         * For example the BlockPos of an entity or a block from which capacity is extracted.
         */
        BlockPos blockPos();

        /**
         * The amount that is currently present.
         */
        float amount();

        /**
         * The amount that is extracted.
         */
        float extraction();

        /**
         * The amount that was requested to be extracted.
         */
        float requestedExtraction();


        static EntityExtractionContext create(
                LivingEntity entity,
                float amount,
                float extraction,
                float requestedExtraction
        ) {
                return new NebulaEntityExtractionContext(
                        entity,
                        entity.getWorld(),
                        entity.getPos(),
                        entity.getBlockPos(),
                        amount,
                        extraction,
                        requestedExtraction
                );
        }
    }