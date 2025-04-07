package dev.louis.nebula.spell;

import dev.louis.nebula.api.event.SpellCastEvent;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SpellCastHelper {
    private SpellCastHelper() {

    }

    public static <Caster, S extends Spell<Caster>> boolean tryCast(SpellSource<? extends Caster> source, S spell, SimpleSpellCaster<Caster> spellCast) {
        if (!SpellCastEvent.BEFORE.invoker().allowSpellCast(source, spell)) return false;

        try {
            spellCast.castSpell(spell, source);
        } catch (SpellFumble e) {
            return false;
        }
        SpellCastEvent.AFTER.invoker().onSpellCast(source, spell);
        return true;
    }

    public static <Caster, S extends Spell<Caster>> boolean tryCast(SpellSource<? extends Caster> source, S spell, TransactionalSpellCaster<Caster> spellCast, Transaction transaction) {
        if (!SpellCastEvent.BEFORE.invoker().allowSpellCast(source, spell)) return false;

        try {
            spellCast.castSpell(spell, source, transaction);
        } catch (SpellFumble e) {
            return false;
        }
        transaction.commit();
        SpellCastEvent.AFTER.invoker().onSpellCast(source, spell);
        return true;
    }


    @ApiStatus.Internal
    private sealed interface SpellCaster<Caster> {

    }
    public non-sealed interface SimpleSpellCaster<Caster> extends SpellCaster<Caster> {
        Spell<Caster> castSpell(Spell<Caster> spell, SpellSource<? extends Caster> source) throws SpellFumble;
    }

    public non-sealed interface TransactionalSpellCaster<Caster> extends SpellCaster<Caster> {
        Spell<Caster> castSpell(Spell<Caster> spell, SpellSource<? extends Caster> source, TransactionContext context) throws SpellFumble;
    }
}
