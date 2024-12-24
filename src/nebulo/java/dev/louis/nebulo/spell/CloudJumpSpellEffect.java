package dev.louis.nebulo.spell;


import dev.louis.nebula.api.spell.effect.SpellEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class CloudJumpSpellEffect extends SpellEffect {

    public CloudJumpSpellEffect(Identifier id) {
        super(id);
    }

    @Override
    public void onActivated(LivingEntity entity) {
        entity.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
        entity.addVelocity(0, 2, 0);
        entity.setVelocity(entity.getVelocity().multiply(1, 0.5, 0).add(0, 2, 0));
        entity.velocityModified = true;
    }

    @Override
    public void tick(LivingEntity entity) {
        var world = entity.getWorld();
        /*if (age % 2 == 0)*/ entity.playSound(SoundEvents.BLOCK_GLASS_HIT, 2f, -1f);

        if (entity.isSneaking() && entity.getVelocity().getY() > -0.1) {
            entity.addVelocity(0, -0.1, 0);
        }

        if(!entity.getWorld().isClient()) {
            ((ServerWorld) entity.getWorld()).spawnParticles(
                    ParticleTypes.CLOUD,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    2,
                    0,
                    1,
                    0,
                    0.1
            );
        }
    }

    @Override
    public void onEnd(LivingEntity entity) {
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

    @Override
    public boolean shouldContinue(ServerWorld serverWorld, LivingEntity entity) {
        return /*age < 15 &&*/ entity.getVelocity().getY() > -0;
    }
}
