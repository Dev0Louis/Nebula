package dev.louis.nebulo.spell;

import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.component.CastComponents;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebulo.NebuloSpellEffects;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class CloudJumpSpell extends Spell<ServerPlayerEntity> {

    public CloudJumpSpell() {
        super(CastComponents.MANA_SOURCE, ((source, comp1, context) ->  CloudJumpSpell.this.tryCast()));;
    }

    public void tryCast(SpellSource<? extends ServerPlayerEntity> source, ManaSource source, TransactionContext context) throws SpellFumble {
        source.expectKilothaum(100, context);
        source.expectSpellEffectStart(NebuloSpellEffects.CLOUD_JUMP, context);
    }

}
