package dev.louis.nebulo;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebulo.spell.CloudJumpSpell;
import net.minecraft.util.Identifier;

public class NebuloSpells {
    public static final SpellType<?> CLOUD_JUMP = SpellType.register(Identifier.of(Nebulo.MOD_ID, "cloud_jump"), SpellType.Builder.create(CloudJumpSpell::new, 2));
    public static void init() {

    }
}
