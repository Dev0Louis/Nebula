package dev.louis.nebulo.spell;

import dev.louis.nebula.api.mana.helper.ManaHelper;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class CloudJumpSpell implements Spell<ServerPlayerEntity> {
    @Override
    public boolean cast(SpellSource<? extends ServerPlayerEntity> source)  {
        try (Transaction t1 = Transaction.openOuter()){
            if (ManaHelper.drainManaOrFail(source.getManaPool(), 1, t1)) return false;
            if (source.getCaster().startSpellEffect(new CloudJumpSpellEffect(source.getCaster()))) {
                t1.commit();
                return true;
            }
            return false;
        }
    }
}
