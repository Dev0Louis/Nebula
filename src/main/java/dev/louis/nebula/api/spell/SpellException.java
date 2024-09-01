package dev.louis.nebula.api.spell;

import java.util.function.Consumer;

public class SpellException extends Exception {

    public void onFail(SpellSource<?> source) {

    }

    public static SpellException create() {
        return new SpellException();
    }

    public static SpellException create(Consumer<SpellSource<?>> failAction) {
        return new SpellException() {
            @Override
            public void onFail(SpellSource<?> source) {
                failAction.accept(source);
            }
        };
    }
}
