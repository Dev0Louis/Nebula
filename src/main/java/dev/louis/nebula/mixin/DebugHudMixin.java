package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
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
		original.add("[Nebula] Mana: " + String.format("%.2f", MinecraftClient.getInstance().player.getManaManager().getMana()));
		original.add("[Nebula] Active Spell Effects: " + MinecraftClient.getInstance().player.getSpellEffects().size());
		return original;
	}
}
