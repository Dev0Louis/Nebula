package dev.louis.nebula.api.spell.quick;

import java.util.function.Consumer;

public class SpellException extends Exception {

    public void onFail(QuickSpellSource<?> source) {

    }

    public static SpellException create() {
        return new SpellException();
    }

    public static SpellException create(Consumer<QuickSpellSource<?>> failAction) {
        return new SpellException() {
            @Override
            public void onFail(QuickSpellSource<?> source) {
                failAction.accept(source);
            }
        };
    }
}
