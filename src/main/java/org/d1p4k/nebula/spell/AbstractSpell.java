package org.d1p4k.nebula.spell;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public abstract class AbstractSpell {
    public int cost;

    public AbstractSpell(int cost) {
        this.cost = cost;
    }

    public boolean checkMana(ServerPlayerEntity player) {
        return((NebulaPlayer) player).getManaManager().decreaseIfEnough(cost);
    }
    public boolean checkKnowledge(ServerPlayerEntity player) {
        return ((NebulaPlayer) player).getSpellKnowledge().isCastable(getID());
    }
    public abstract void cast(ServerPlayerEntity player);
    public abstract Identifier getID();

}
