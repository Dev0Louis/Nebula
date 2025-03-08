package dev.louis.nebula.api.spell.component;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@SuppressWarnings("InstantiationOfUtilityClass")
public class CastComponent<Value> {
    public static <Value> CastComponent<Value> create() {
        return new CastComponent<>();
    }
}
