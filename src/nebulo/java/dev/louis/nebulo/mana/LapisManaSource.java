package dev.louis.nebulo.mana;

import dev.louis.nebula.api.mana.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class LapisManaSource implements ManaSource {

    private final PlayerEntity player;

    public LapisManaSource(PlayerEntity player) {
        this.player = player;
    }

    public static LapisManaSource create(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) return new LapisManaSource(player);
        return null;
    }

    @Override
    public float extractMana(float extraction, TransactionContext context) {
        var roundedUpExtraction = MathHelper.ceil(extraction);
        var actualRemoval = player.getInventory().remove(stack -> stack.isOf(Items.LAPIS_LAZULI), roundedUpExtraction, player.playerScreenHandler.getCraftingInput());
        if (actualRemoval == roundedUpExtraction) return extraction;

        return actualRemoval;
    }
}
