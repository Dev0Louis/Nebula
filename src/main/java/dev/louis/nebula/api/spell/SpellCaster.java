package dev.louis.nebula.api.spell;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.World;

public interface SpellCaster<Caster extends SpellCaster<Caster>> {
    void castSpell(Spell<Caster> spell, Transaction transaction) throws SpellException;

    default void castSpell(Spell<Caster> spell) {
        try(Transaction transaction = Transaction.openOuter()) {
            castSpell(spell, transaction);
        } catch (SpellException e) {
            spell.fail(this.nebula$getCaster());
        }
    }

    Caster nebula$getCaster();

    World getWorld();
}
