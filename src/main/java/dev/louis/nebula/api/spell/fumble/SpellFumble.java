package dev.louis.nebula.api.spell.fumble;

import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * The reason is unused by default, but in future or by other mods be displayed to the user.
 */
public class SpellFumble extends Throwable {
  private static final Text UNKNOWN_REASON = Text.translatable("spell_exception.nebula.unknown");
  public static final Text MANA = Text.translatable("spell_exception.nebula.insufficient_mana");
  public static final Text SPELL_EFFECT = Text.translatable("spell_exception.nebula.already_running_spell_effect");

  private final Supplier<Text> reason;

  public SpellFumble() {
    this(() -> UNKNOWN_REASON);
  }

  public SpellFumble(Supplier<Text> reason) {
    this.reason = reason;
  }

  public Text getReason() {
    return reason.get();
  }

  public static SpellFumble manaFumble() {
    return new SpellFumble(() -> MANA);
  }

  public static SpellFumble spellEffectFumble() {
    return new SpellFumble(() -> SPELL_EFFECT);
  }
}
