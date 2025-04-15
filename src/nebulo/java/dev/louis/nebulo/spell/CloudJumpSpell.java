package dev.louis.nebulo.spell;

import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.api.spell.component.CastComponents;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebulo.NebuloSpellEffects;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class CloudJumpSpell {

    public void tryCast(ServerWorld world, ServerPlayerEntity player) {
        player.getManaManager().expectKilothaum(100, context);
        source.expectSpellEffectStart(NebuloSpellEffects.CLOUD_JUMP, context);
    }

}
