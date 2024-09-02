package dev.louis.nebula.api.spell;

import dev.louis.nebula.spell.EntitySpellSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SpellSource<Source> {
    void castSpell(Spell<SpellSource<Source>> spell);

    boolean isActive();
    World getWorld();
    Vec3d getPos();
    BlockPos getBlockPos();
    Source getSource();

    static <E extends LivingEntity> SpellSource<E> ofEntity(E entity) {
        return new EntitySpellSource<>(entity);
    }

    static <P extends PlayerEntity> SpellSource<P> ofPlayer(P player) {
        return new EntitySpellSource<>(player);
    }
}
