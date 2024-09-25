package dev.louis.nebula.mixin.helper;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.helper.LivingEntitySpellCaster;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntitySpellCaster {
    @Override
    public void castSpell(Spell<LivingEntity> spell) {
        spell.tryCast(SpellSource.of((LivingEntity) (Object) this));
    }
}
