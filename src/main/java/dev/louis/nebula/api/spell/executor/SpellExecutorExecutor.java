package dev.louis.nebula.api.spell.executor;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface SpellExecutorExecutor {
    boolean startSpellExecutor(SpellExecutor executor);

    boolean stopSpellExecutorIf(Predicate<SpellExecutor> stopper);

    default boolean stopSpellExecutorsOf(SpellExecutorType<?> spellExecutorType) {
        return stopSpellExecutorIf(executor -> executor.getType().equals(spellExecutorType));
    }

    Stream<SpellExecutor> streamSpellExecutors();
}
