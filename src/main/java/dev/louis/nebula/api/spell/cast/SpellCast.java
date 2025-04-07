package dev.louis.nebula.api.spell.cast;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.component.CastComponent;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Map;

public interface SpellCast<Caster, SPELL extends Spell<Caster>> {
        <V> void tryCast(SPELL spell, SpellSource<? extends Caster> source, Map<CastComponent<V>, V> comp1, TransactionContext context) throws SpellFumble;

        interface SpellCast1<Caster, T1> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast2<Caster, T1, T2> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast3<Caster, T1, T2, T3> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast4<Caster, T1, T2, T3, T4> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast5<Caster, T1, T2, T3, T4, T5> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast6<Caster, T1, T2, T3, T4, T5, T6> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast7<Caster, T1, T2, T3, T4, T5, T6, T7> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast8<Caster, T1, T2, T3, T4, T5, T6, T7, T8> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast9<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast10<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast11<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast12<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast13<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast14<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast15<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast16<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast17<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast18<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast19<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast20<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast21<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast22<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast23<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast24<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast25<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast26<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, T26 comp26, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast27<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, T26 comp26, T27 comp27, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast28<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, T26 comp26, T27 comp27, T28 comp28, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast29<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, T26 comp26, T27 comp27, T28 comp28, T29 comp29, TransactionContext context) throws SpellFumble;
        }
        interface SpellCast30<Caster, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30> {
                void tryCast(SpellSource<? extends Caster> source, T1 comp1, T2 comp2, T3 comp3, T4 comp4, T5 comp5, T6 comp6, T7 comp7, T8 comp8, T9 comp9, T10 comp10, T11 comp11, T12 comp12, T13 comp13, T14 comp14, T15 comp15, T16 comp16, T17 comp17, T18 comp18, T19 comp19, T20 comp20, T21 comp21, T22 comp22, T23 comp23, T24 comp24, T25 comp25, T26 comp26, T27 comp27, T28 comp28, T29 comp29, T30 comp30, TransactionContext context) throws SpellFumble;
        }
}