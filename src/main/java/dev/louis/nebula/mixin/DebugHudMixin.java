package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.nebula.api.mana.helper.ManaHelper;
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

		original.add("[Nebula] " + ManaHelper.formatKilothaum(MinecraftClient.getInstance().player.getManaManager().getThaum()) + "/" + ManaHelper.formatKilothaum(MinecraftClient.getInstance().player.getManaManager().getThaumCapacity()) + " Kilothaum");
		original.add("[Nebula] Active Spell Effects: " + MinecraftClient.getInstance().player.getSpellEffects().size());
		return original;
	}


	
	

}
