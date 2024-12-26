package dev.louis.nebulo.mana;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
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

public class LapisManaSource extends SnapshotParticipant<Integer> implements EntityManaPool {
    public static final EntityManaPoolType TYPE = EntityManaPoolType.create(LapisManaSource::create);
    private final PlayerEntity player;
    private int consumedLapis;
    private boolean enabled = true;

    public LapisManaSource(PlayerEntity player) {
        this.player = player;
    }

    public static LapisManaSource create(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) return new LapisManaSource(player);
        return null;
    }

    @Override
    public float insertMana(float insertion, TransactionContext context) {
        return 0;
    }

    @Override
    public float extractMana(float extraction, TransactionContext context) {
        if (!enabled) return 0;
        updateSnapshots(context);
        var consumedLapis = MathHelper.ceil(extraction);
        long available = PlayerInventoryStorage.of(player).extract(ItemVariant.of(Items.LAPIS_LAZULI), consumedLapis, context);

        // We shall never return more than extraction!
        return Math.min(available, extraction);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.enabled = nbt.getBoolean("enabled");
    }

    @Override
    public float getMana() {
        if (!enabled) return 0;
        return player.getInventory().count(Items.LAPIS_LAZULI);
    }

    @Override
    public float getCapacity() {
        if (!enabled) return 0;
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

    @Override
    public EntityManaPoolType getType() {
        return TYPE;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }
}
