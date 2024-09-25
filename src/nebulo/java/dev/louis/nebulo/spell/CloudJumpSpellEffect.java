package dev.louis.nebulo.spell;


import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.nebulo.NebuloSpellEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class CloudJumpSpellEffect extends SpellEffect {
    public CloudJumpSpellEffect(LivingEntity entity) {
        super(NebuloSpellEffects.CLOUD_JUMP, entity);
    }

    @Override
    public void onStart() {
        this.target.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
        this.target.addVelocity(0, 2, 0);
        this.target.velocityModified = true;
    }

    @Override
    public void tick() {
        var world = this.target.getWorld();
        if (age % 2 == 0) this.target.playSound(SoundEvents.BLOCK_GLASS_HIT, 2f, -1f);

        if (this.target.isSneaking() && this.target.getVelocity().getY() > -0.1) {
            this.target.addVelocity(0, -0.1, 0);
        }

        if(!world.isClient()) {
            ((ServerWorld) target.getWorld()).spawnParticles(
                    ParticleTypes.CLOUD,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    2,
                    0,
                    1,
                    0,
                    0.1
            );
        }
    }

    @Override
    public void onEnd() {
        target.playSound(SoundEvents.ENTITY_CAMEL_DASH, 2f, -1f);
        if(!this.target.getWorld().isClient()) {
            ((ServerWorld) target.getWorld()).spawnParticles(
                    ParticleTypes.SMOKE,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    5,
                    0,
                    1,
                    0,
                    0.1
            );

        }
    }

    @Override
    public boolean shouldContinue() {
        return this.target.getVelocity().getY() <= 0;
    }
}
