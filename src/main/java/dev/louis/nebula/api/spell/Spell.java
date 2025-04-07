package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.spell.cast.SpellCast;
import dev.louis.nebula.api.spell.cast.SpellCast.*;
import dev.louis.nebula.api.spell.component.CastComponent;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Map;

public abstract class Spell<Caster> {

    private final SpellCast<Caster> cast;

    protected <T1> Spell(CastComponent<T1> comp, SpellCast1<Caster, T1> spellCast1) {
        this.cast = new SpellCast<Caster, >() {
            @Override
            public void tryCast(Spell spell, SpellSource source, Map comp1, TransactionContext context) throws SpellFumble {

            }

            @Override
            public <V> void tryCast(SpellSource<? extends Caster> source, Map<CastComponent<V>, V> componentMap, TransactionContext context) throws SpellFumble {
                spellCast1.tryCast(source, expectComp(comp, componentMap), context);
            }
        };
    }
    protected <T1, T2> Spell(CastComponent<T1> comp1, CastComponent<T2> comp2, SpellCast2<Caster, T1, T2> spellCast1) {
        this.cast = new SpellCast<Caster>() {
            @Override
            public <V> void tryCast(SpellSource<? extends Caster> source, Map<CastComponent<V>, V> componentMap, TransactionContext context) throws SpellFumble {
                spellCast1.tryCast(source, expectComp(comp1, componentMap), expectComp(comp2, componentMap), context);
            }
        };
    }
    protected <T1, T2, T3> Spell(CastComponent<T1> comp1, CastComponent<T2> comp2, CastComponent<T3> comp3, SpellCast3<Caster, T1, T2, T3> spellCast1) {
        this.cast = new SpellCast<Caster>() {
            @Override
            public <V> void tryCast(SpellSource<? extends Caster> source, Map<CastComponent<V>, V> componentMap, TransactionContext context) throws SpellFumble {
                spellCast1.tryCast(source, expectComp(comp1, componentMap), expectComp(comp2, componentMap), expectComp(comp3, componentMap), context);
            }
        };
    }


    final <T, V> T expectComp(CastComponent<T> comp, Map<CastComponent<V>, V> compMap) throws SpellFumble {
        T value = (T) compMap.get(comp);
        if (value == null) throw SpellFumble.componentMissing();
        return value;
    }

    final void cast(Runnable cast, TransactionContext context) {
        context.addOuterCloseCallback(result -> {
            if (result.wasCommitted()) {
                cast.run();
            }
        });
    }


}
