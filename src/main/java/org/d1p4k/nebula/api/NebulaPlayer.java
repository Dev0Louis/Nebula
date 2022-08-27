package org.d1p4k.nebula.api;

import net.minecraft.util.Identifier;
import org.d1p4k.nebula.knowledge.SpellKnowledge;
import org.d1p4k.nebula.mana.Mana;

import java.util.List;

public interface NebulaPlayer {
    public void setManaManger(Mana mana);
    public Mana getManaManger();
    public int getMana();
    public void setMana(int mana);

    public List<Identifier> getCastableSpells();
    public void setCastableSpells(List<Identifier> castableSpells);
    public SpellKnowledge getSpellKnowledge();
}
