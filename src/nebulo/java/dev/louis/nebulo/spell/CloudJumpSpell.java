package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.transaction.SpellEffectWrapper;
import dev.louis.nebula.api.spell.exception.SpellFumble;
import dev.louis.nebulo.NebuloSpellEffects;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class CloudJumpSpell implements Spell<ServerPlayerEntity> {

    @Override
    public void cast(SpellSource<? extends ServerPlayerEntity> source, TransactionContext context) throws SpellFumble {
        source.drainKilomana(1, context);
        source.startSpellEffect(NebuloSpellEffects.CLOUD_JUMP, context);
    }

}
