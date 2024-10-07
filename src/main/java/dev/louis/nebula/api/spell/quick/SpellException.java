package dev.louis.nebula.api.spell.quick;

public class SpellException extends Exception {
    public static SpellException create() {
        return new SpellException();
    }
}
