package dev.louis.nebula.api.spell.component;

@SuppressWarnings("InstantiationOfUtilityClass")
public class CastComponent<Data> {
    public static <LData> CastComponent<LData> create() {
        return new CastComponent<>();
    }
}
