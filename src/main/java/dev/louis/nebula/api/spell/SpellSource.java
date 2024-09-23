package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.spell.source.BlockEntitySpellSource;
import dev.louis.nebula.spell.source.EntitySpellSource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SpellSource<Caster> {
    void castSpell(Spell<Caster> spell);

    World getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Caster getCaster();
    ManaPool getManaPool();


    static <E extends Entity> SpellSource<E> of(E entity, World world, Vec3d pos, BlockPos blockPos) {
        return new EntitySpellSource<>(entity, world, pos, blockPos);
    }

    static <E extends LivingEntity> SpellSource<E> of(E entity) {
        return of(entity, entity.getWorld(), entity.getPos(), entity.getBlockPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(BE blockEntity) {
        return of(blockEntity, blockEntity.getWorld(), blockEntity.getPos().toCenterPos(), blockEntity.getPos());
    }

    static <BE extends BlockEntity> SpellSource<BE> of(BE blockEntity, World world, Vec3d pos, BlockPos blockPos) {
        return new BlockEntitySpellSource<>(blockEntity, world, pos, blockPos);
    }
}
