package org.d1p4k.nebula.spell;

import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public abstract class AbstractSpell {
    public Identifier spellIdentifier;
    NebulaPlayer player;
    int cost;

    public AbstractSpell(NebulaPlayer player, Identifier spellIdentifier, int cost) {
        this.player = player;
        this.cost = cost;
        this.spellIdentifier = spellIdentifier;
    }

    public boolean checkMana() {
        return player.getManaManger().decreaseIfEnough(cost);
    }
    public boolean checkKnowledge() {
        return player.getSpellKnowledge().isCastable(spellIdentifier);
    }
    public abstract void cast(NebulaPlayer player);

}
