package dev.louis.nebulo;

import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.nebulo.spell.CloudJumpSpellEffect;
import net.minecraft.util.Identifier;

public class NebuloSpellEffects {
    public static final SpellEffectType<CloudJumpSpellEffect> CLOUD_JUMP = SpellEffectType.register(Identifier.of(Nebulo.MOD_ID, "cloud_jump"), CloudJumpSpellEffect::new);
    public static void init() {

    }
}
