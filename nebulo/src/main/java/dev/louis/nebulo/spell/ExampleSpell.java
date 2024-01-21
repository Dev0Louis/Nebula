package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ExampleSpell extends Spell {
    public ExampleSpell(SpellType<?> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        this.getCaster().playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1f, 1f);
    }

    @Override
    public void tick() {
        this.getCaster().setVelocity(this.getCaster().getVelocity().multiply(0.75f).add(this.getCaster().getRotationVector()));
        this.getCaster().velocityModified = true;
    }

    @Override
    public int getDuration() {
        return 10;
    }

    @Override
    public void onEnd() {
        this.getCaster().playSound(SoundEvents.ENTITY_CAMEL_DASH, SoundCategory.PLAYERS, 2f, -1f);
    }
}
