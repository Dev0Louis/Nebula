package dev.louis.nebula.api.spell.source;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellException;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.TickingSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntitySpellSource<Entity extends LivingEntity> implements SpellSource<Entity> {
    protected Entity entity;

    public EntitySpellSource(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void castSpell(Spell<SpellSource<Entity>> spell) {
        try {
            spell.cast(this);
            if (spell instanceof TickingSpell<SpellSource<Entity>> tickingSpell) {
                tickingSpell.canStart(this, );
            }
        } catch (SpellException e) {
            e.onFail(this);
        }
    }

    @Override
    public boolean isActive() {
        return entity.isAlive();
    }

    @Override
    public World getWorld() {
        return entity.getWorld();
    }

    @Override
    public Vec3d getPos() {
        return entity.getPos();
    }

    @Override
    public BlockPos getBlockPos() {
        return entity.getBlockPos();
    }

    @Override
    public Entity getSource() {
        return entity;
    }
}
