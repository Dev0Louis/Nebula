package org.d1p4k.nebula.mana.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ManaRegenerationStatusEffect extends StatusEffect {
    public ManaRegenerationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x98D982);
    }


    // This method is called every tick to check whether it should apply the status effect or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).addExperienceLevels(1 + amplifier);
            //((NebulaPlayer) entity).getManaManager().add(1 << amplifier);
        }
    }

}
