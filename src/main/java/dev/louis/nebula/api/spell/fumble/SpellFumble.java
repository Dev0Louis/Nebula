package dev.louis.nebula.api.spell.fumble;

import net.minecraft.text.Text;

import java.util.function.Supplier;

public class SpellFumble extends Throwable {
  private static final Text UNKNOWN_REASON = Text.translatable("spell_exception.nebula.unknown");
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
}
