package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.nebula.manager.NebulaManager;
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
		original.add("[Nebula] Mana Manager Mod: " + NebulaManager.getManaManagerMod().getMetadata().getName());
		original.add("[Nebula] Spell Manager Mod: " + NebulaManager.getSpellManagerMod().getMetadata().getName());
		return original;
	}
}
