package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {

	@ModifyReturnValue(
			at = @At("RETURN"),
			method = "getLeftText"
	)
	protected List<String> getLeftText(List<String> original) {

		original.add("[Nebula] Mana: " + formatFloat(MinecraftClient.getInstance().player.getManaManager().getMana()) + "/"+formatFloat(MinecraftClient.getInstance().player.getManaManager().getCapacity()));
		original.add("[Nebula] Active Spell Effects: " + MinecraftClient.getInstance().player.getSpellEffects().size());
		return original;
	}

	private static @NotNull String formatFloat(float mana) {
		if (Float.isInfinite(mana)) {
			return mana < 0 ? "-∞" : "+∞";
		} else {
			return String.format("%.2f", MinecraftClient.getInstance().player.getManaManager().getMana());
		}
	}
}
