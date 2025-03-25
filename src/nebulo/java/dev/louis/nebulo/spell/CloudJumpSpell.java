package dev.louis.nebulo.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebulo.NebuloSpellEffects;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class CloudJumpSpell implements Spell<ServerPlayerEntity> {

    @Override
    public void tryCast(SpellSource<? extends ServerPlayerEntity> source, TransactionContext context) throws SpellFumble {
        source.expectKilothaum(1, context);
        source.expectSpellEffectStart(NebuloSpellEffects.CLOUD_JUMP, context);
    }

}
