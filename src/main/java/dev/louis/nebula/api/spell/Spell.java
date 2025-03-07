package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface Spell<Caster> {

    /**
     * This should not be called manually unless you are a SpellCaster.
     */
    void tryCast(SpellSource<? extends Caster> source, TransactionContext context) throws SpellFumble;

    default void cast(Runnable cast, TransactionContext context) {
        context.addOuterCloseCallback(result -> {
            if (result.wasCommitted()) {
                cast.run();
            }
        });
    }
}
