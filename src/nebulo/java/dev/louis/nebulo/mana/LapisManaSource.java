package dev.louis.nebulo.mana;

import dev.louis.nebula.api.mana.pool.entity.EntityManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class LapisManaSource implements EntityManaPool {
    public static final EntityManaPoolType TYPE = EntityManaPoolType.create(LapisManaSource::create);
    private final PlayerEntity player;
    private long storedMana;
    private boolean enabled = true;

    public LapisManaSource(PlayerEntity player) {
        this.player = player;
    }

    public static LapisManaSource create(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) return new LapisManaSource(player);
        return null;
    }

    @Override
    public long insertMana(long insertion, TransactionContext context) {
        return 0;
    }

    @Override
    public long extractMana(long extraction, TransactionContext context) {
        if (!enabled) return 0;
        if (storedMana < extraction) {
            var lapisExtraction = extraction - storedMana;
            var consumedLapis = MathHelper.ceil(lapisExtraction / 1000f);

            long available = PlayerInventoryStorage.of(player).extract(ItemVariant.of(Items.LAPIS_LAZULI), consumedLapis, context) * 1000;
            storedMana = storedMana + available;
        }

        // We shall never return more than extraction!
        var result = Math.min(storedMana, extraction);
        this.storedMana = this.storedMana - result;
        return result;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("enabled", enabled);
        nbt.putLong("storedMana", storedMana);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.enabled = nbt.getBoolean("enabled");
        this.storedMana = nbt.getLong("storedMana");
    }

    @Override
    public long getMana() {
        if (!enabled) return 0;
        return player.getInventory().count(Items.LAPIS_LAZULI) * 1000L + storedMana;
    }

    @Override
    public long getCapacity() {
        if (!enabled) return 0;
        return player.getInventory().count(Items.LAPIS_LAZULI) * 1000L + storedMana;
    }

    @Override
    public EntityManaPoolType getType() {
        return TYPE;
    }

    @Override
    public void tick() {
        this.storedMana--;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }
}
