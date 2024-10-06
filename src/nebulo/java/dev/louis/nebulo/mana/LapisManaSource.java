package dev.louis.nebulo.mana;

import dev.louis.nebula.api.mana.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class LapisManaSource extends SnapshotParticipant<Integer> implements ManaSource {

    private final PlayerEntity player;
    private int consumedLapis;

    public LapisManaSource(PlayerEntity player) {
        this.player = player;
    }

    public static LapisManaSource create(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) return new LapisManaSource(player);
        return null;
    }

    @Override
    public float extractMana(float extraction, TransactionContext context) {
        updateSnapshots(context);
        consumedLapis = MathHelper.ceil(extraction);
        long available = PlayerInventoryStorage.of(player).extract(ItemVariant.of(Items.LAPIS_LAZULI), consumedLapis, context);

        // We shall never return more than extraction!
        return Math.min(available, extraction);
    }

    @Override
    protected Integer createSnapshot() {
        return consumedLapis;
    }

    @Override
    protected void readSnapshot(Integer snapshot) {
        this.consumedLapis = snapshot;
    }
}
