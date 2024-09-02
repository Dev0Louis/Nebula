package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.InsertionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public record NebulaInsertionContext(
        Optional<LivingEntity> entity,
        World world,
        Vec3d pos,
        BlockPos blockPos,
        float amount,
        float insertion,
        float requestedInsertion
) implements InsertionContext {

}
