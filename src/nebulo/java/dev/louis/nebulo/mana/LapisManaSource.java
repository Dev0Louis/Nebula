package dev.louis.nebulo.mana;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class LapisManaSource extends SnapshotParticipant<Integer> implements ManaPool {

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
    public float insertMana(ServerWorld world, float insertion, TransactionContext context) {
        return 0;
    }

    @Override
    public float extractMana(ServerWorld world, float extraction, TransactionContext context) {
        updateSnapshots(context);
        var consumedLapis = MathHelper.ceil(extraction);
        long available = PlayerInventoryStorage.of(player).extract(ItemVariant.of(Items.LAPIS_LAZULI), consumedLapis, context);

        // We shall never return more than extraction!
        return Math.min(available, extraction);
    }

    @Override
    public float getMana() {
        return player.getInventory().count(Items.LAPIS_LAZULI);
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
