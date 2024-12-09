package dev.louis.nebulo;

import dev.louis.nebula.api.spell.effect.SpellEffects;
import dev.louis.nebulo.spell.CloudJumpSpellEffect;
import net.minecraft.util.Identifier;

public class NebuloSpellEffects {
    public static final CloudJumpSpellEffect CLOUD_JUMP = SpellEffects.register(
            Identifier.of(Nebulo.MOD_ID, "cloud_jump"),
            CloudJumpSpellEffect::new
    );

    public static void init() {

    }
}
