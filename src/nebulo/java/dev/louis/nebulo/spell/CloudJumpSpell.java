package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.entity.player.PlayerEntity;

public class CloudJumpSpell implements Spell<PlayerEntity> {
    @Override
    public void cast(SpellSource<PlayerEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::create);
        Spell.drainMana(manaPool, 1);
        source.getCaster().startSpellEffect(new CloudJumpSpellEffect(source.getCaster()));
    }
}
