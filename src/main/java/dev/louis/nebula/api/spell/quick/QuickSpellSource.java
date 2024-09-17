package dev.louis.nebula.api.spell.quick;

import dev.louis.nebula.spell.source.BlockEntityQuickSpellSource;
import dev.louis.nebula.spell.source.EntityQuickSpellSource;
import dev.louis.nebula.spell.source.WorldQuickSpellSource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface QuickSpellSource<Caster> {
    void castSpell(QuickSpell<Caster> quickSpell);

    World getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();


    static QuickSpellSource<World> universal(World world, Vec3d pos) {
        return new WorldQuickSpellSource(world, pos);
    }

    static <E extends LivingEntity> QuickSpellSource<E> entity(E entity, World world, Vec3d pos, BlockPos blockPos) {
        return new EntityQuickSpellSource<>(entity, world, pos, blockPos);
    }

    static <E extends LivingEntity> QuickSpellSource<E> entity(E entity) {
        return entity(entity, entity.getWorld(), entity.getPos(), entity.getBlockPos());
    }

    static <P extends PlayerEntity> QuickSpellSource<P> player(P player, World world, Vec3d pos, BlockPos blockPos) {
        return entity(player, player.getWorld(), player.getPos(), player.getBlockPos());
    }

    static <P extends PlayerEntity> QuickSpellSource<P> player(P player) {
        return entity(player);
    }

    static <BE extends BlockEntity> QuickSpellSource<BE> blockEntity(BE blockEntity) {
        return blockEntity(blockEntity, blockEntity.getWorld(), blockEntity.getPos().toCenterPos(), blockEntity.getPos());
    }

    static <BE extends BlockEntity> QuickSpellSource<BE> blockEntity(BE blockEntity, World world, Vec3d pos, BlockPos blockPos) {
        return new BlockEntityQuickSpellSource<>(blockEntity, world, pos, blockPos);
    }
}
