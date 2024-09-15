package dev.louis.nebula.api.spell;

import dev.louis.nebula.spell.source.BlockEntitySpellSource;
import dev.louis.nebula.spell.source.EntitySpellSource;
import dev.louis.nebula.spell.source.WorldSpellSource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SpellSource<Caster> {
    void castSpell(QuickSpell<Caster> quickSpell);

    World getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();


    static SpellSource<World> universal(World world, Vec3d pos) {
        return new WorldSpellSource(world, pos);
    }

    static <E extends LivingEntity> SpellSource<E> entity(E entity, World world, Vec3d pos, BlockPos blockPos) {
        return new EntitySpellSource<>(entity, world, pos, blockPos);
    }

    static <E extends LivingEntity> SpellSource<E> entity(E entity) {
        return entity(entity, entity.getWorld(), entity.getPos(), entity.getBlockPos());
    }

    static <P extends PlayerEntity> SpellSource<P> player(P player, World world, Vec3d pos, BlockPos blockPos) {
        return entity(player, player.getWorld(), player.getPos(), player.getBlockPos());
    }

    static <P extends PlayerEntity> SpellSource<P> player(P player) {
        return entity(player);
    }

    static <BE extends BlockEntity> SpellSource<BE> blockEntity(BE blockEntity) {
        return blockEntity(blockEntity, blockEntity.getWorld(), blockEntity.getPos().toCenterPos(), blockEntity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> blockEntity(BE blockEntity, World world, Vec3d pos, BlockPos blockPos) {
        return new BlockEntitySpellSource<>(blockEntity, world, pos, blockPos);
    }
}
