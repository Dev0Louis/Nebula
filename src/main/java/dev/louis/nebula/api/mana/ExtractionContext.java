package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.NebulaExtractionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.NonExtendable
public interface ExtractionContext {
        /**
         * The entity that mana is being extracted from.
         */
        Optional<LivingEntity> entity();

        /**
         * The player that mana is being extracted from.
         */
        default Optional<PlayerEntity> player() {
            return entity().filter(PlayerEntity.class::isInstance).map(PlayerEntity.class::cast);
        }

        /**
         * The world that the extraction is taking place in.
         */
        World world();

        /**
         * The position at which the mana is extracted from.
         * For example the pos of an entity or a block from which mana is extracted.
         */
        Vec3d pos();

        /**
         * The BlockPos at which the mana is extracted from.
         * For example the BlockPos of an entity or a block from which mana is extracted.
         */
        BlockPos blockPos();

        /**
         * The amount that is currently present.
         */
        float amount();

        /**
         * The amount that should be extracted.
         */
        float extraction();

        /**
         * The amount that was requested to be extracted.
         */
        float requestedExtraction();

        static ExtractionContext create(
                World world,
                Vec3d pos,
                BlockPos blockPos,
                float amount,
                float extraction,
                float requestedExtraction
        ) {
                return new NebulaExtractionContext(
                        Optional.empty(),
                        world,
                        pos,
                        blockPos,
                        amount,
                        extraction,
                        requestedExtraction
                );
        }

        static ExtractionContext create(
                LivingEntity entity,
                float amount,
                float extraction,
                float requestedExtraction
        ) {
                return new NebulaExtractionContext(
                        Optional.of(entity),
                        entity.getWorld(),
                        entity.getPos(),
                        entity.getBlockPos(),
                        amount,
                        extraction,
                        requestedExtraction
                );
        }
    }