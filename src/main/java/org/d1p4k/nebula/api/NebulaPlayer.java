package org.d1p4k.nebula.api;

import org.d1p4k.nebula.knowledge.SpellKnowledge;
import org.d1p4k.nebula.mana.Mana;

public interface NebulaPlayer {
    Mana getManaManager();
    void setManaManager(Mana mana);
    public int getMana();

    public void setMana(int mana);
    public SpellKnowledge getSpellKnowledge();
}
