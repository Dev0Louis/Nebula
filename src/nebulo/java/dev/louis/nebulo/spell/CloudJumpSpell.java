package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;

public class CloudJumpSpell implements Spell<PlayerEntity> {
    @Override
    public void cast(SpellSource<PlayerEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::create);
        try(Transaction transaction = Transaction.openOuter()) {
            Spell.drainMana(manaPool, 1, transaction);
            var startedSpellEffect = source.getCaster().startSpellEffect(new CloudJumpSpellEffect(source.getCaster()));
            if (startedSpellEffect) transaction.commit();
        }
    }
}
