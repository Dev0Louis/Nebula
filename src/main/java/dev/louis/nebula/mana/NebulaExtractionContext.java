package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.ExtractionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public record NebulaExtractionContext(
        Optional<LivingEntity> entity,
        World world,
        Vec3d pos,
        BlockPos blockPos,
        float amount,
        float extraction,
        float requestedExtraction
) implements ExtractionContext {

}
