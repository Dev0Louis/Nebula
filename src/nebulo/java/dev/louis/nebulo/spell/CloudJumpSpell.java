package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebulo.NebuloSpellEffects;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.network.ServerPlayerEntity;

public class CloudJumpSpell implements Spell<ServerPlayerEntity> {

    @Override
    public boolean cast(SpellSource<? extends ServerPlayerEntity> source)  {
        try (Transaction t1 = Transaction.openOuter()) {
            if (!source.drainMana(1, t1)) return false;
            if (!source.getCaster().startSpellEffect(NebuloSpellEffects.CLOUD_JUMP)) return false;

            t1.commit();
            return true;
        }
    }
}
