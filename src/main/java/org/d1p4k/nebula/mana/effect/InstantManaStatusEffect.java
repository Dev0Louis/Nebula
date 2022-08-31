package org.d1p4k.nebula.mana.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class InstantManaStatusEffect extends InstantStatusEffect {
    public InstantManaStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x98D982);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).addExperienceLevels(5 << amplifier);
        }
    }
}
