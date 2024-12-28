package dev.louis.nebulo.spell;


import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.Action;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class CloudJumpSpellEffect extends SpellEffect {

    public CloudJumpSpellEffect(RegistryKey<SpellEffect> key) {
        super(key);
    }

    @Override
    public void onActivated(LivingEntity entity) {
        entity.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
        entity.addVelocity(0, 2, 0);
        entity.setVelocity(entity.getVelocity().multiply(1, 0.5, 0).add(0, 2, 0));
        entity.velocityModified = true;
    }

    @Override
    public Action tick(LivingEntity entity, int age) {
        if (age > 20) return Action.STOP;
        if (entity.isSneaking()) {
            if (entity.getVelocity().getY() <= 0) return Action.STOP;
            entity.addVelocity(0, -0.1, 0);
        }

        entity.playSound(SoundEvents.BLOCK_GLASS_HIT, 2f, -1f);
        entity.getWorld().addParticle(
                ParticleTypes.CLOUD,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                (entity.getWorld().random.nextFloat() - 0.5) / 4,
                0,
                (entity.getWorld().random.nextFloat() - 0.5) / 4
        );
        return Action.CONTINUE;
    }

    @Override
    public void onDeactivated(LivingEntity entity) {
        entity.playSound(SoundEvents.ENTITY_CAMEL_DASH, 2f, -1f);
        if(!entity.getWorld().isClient()) {
            ((ServerWorld) entity.getWorld()).spawnParticles(
                    ParticleTypes.SMOKE,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    5,
                    0,
                    1,
                    0,
                    0.1
            );

        }
    }
}
