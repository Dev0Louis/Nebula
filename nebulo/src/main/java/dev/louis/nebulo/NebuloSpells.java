package dev.louis.nebulo;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebulo.spell.ExampleSpell;
import net.minecraft.util.Identifier;

public class NebuloSpells {
    public static final SpellType<?> EXAMPLE_SPELL = SpellType.register(new Identifier(Nebulo.MOD_ID, "example"), SpellType.Builder.create(ExampleSpell::new, 2));
    public static void init() {

    }
}
