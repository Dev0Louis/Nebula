package dev.louis.nebula.spell;

import dev.louis.nebula.api.event.SpellCastEvent;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SpellCastHelper {
    private SpellCastHelper() {

    }

    public static <Caster> boolean tryCast(SpellSource<Caster> source, Spell<Caster> spell, Transaction transaction) {
        if (!SpellCastEvent.BEFORE.invoker().allowSpellCast(source, spell)) return false;

        try {
            spell.cast(source, transaction);
        } catch (SpellFumble e) {
            return false;
        }
        SpellCastEvent.AFTER.invoker().onSpellCast(source, spell);
        transaction.commit();
        return true;
    }
}
