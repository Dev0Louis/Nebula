package org.d1p4k.nebula.spell;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public abstract class AbstractSpell {
    public Identifier spellIdentifier;
    public ServerPlayerEntity player;
    public int cost;

    public AbstractSpell(ServerPlayerEntity player, Identifier spellIdentifier, int cost) {
        this.player = player;
        this.cost = cost;
        this.spellIdentifier = spellIdentifier;
    }

    public boolean checkMana() {
        return((NebulaPlayer) player).getManaManager().decreaseIfEnough(cost);
    }
    public boolean checkKnowledge() {
        return ((NebulaPlayer) player).getSpellKnowledge().isCastable(spellIdentifier);
    }
    public abstract void cast();
    public abstract Identifier getID();

}
