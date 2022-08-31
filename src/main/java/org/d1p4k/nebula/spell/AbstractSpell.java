package org.d1p4k.nebula.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public abstract class AbstractSpell {
    public Identifier spellIdentifier;
    public PlayerEntity player;
    public int cost;

    public AbstractSpell(PlayerEntity player, Identifier spellIdentifier, int cost) {
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

}
