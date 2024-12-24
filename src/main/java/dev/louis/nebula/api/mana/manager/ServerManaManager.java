package dev.louis.nebula.api.mana.manager;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;

public class ServerManaManager implements ManaPool, ManaManager {
    private LivingEntity entity;
    private HashMap<RegistryEntry<EntityManaPoolType>, ManaPool> manaPools;

    public ServerManaManager(LivingEntity entity, HashMap<RegistryEntry<EntityManaPoolType>, ManaPool> manaPools) {
        this.entity = entity;
        this.manaPools = manaPools;
    }

    public static ServerManaManager createManaManager(LivingEntity entity) {
        return new ServerManaManager(
                entity,
                EntityManaPoolRegistererImpl.INSTANCE.createManaPool(entity)
        );
    }

    public void tick() {

    }

    @Override
    public float getMana() {
        return (float) manaPools.values().stream().mapToDouble((manaPool) -> (double) manaPool.getMana()).sum();
    }

    @Override
    public float insertMana(ServerWorld world, float requestedInsertion, TransactionContext context) {
        if (requestedInsertion < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        // This local is going to get modified throughout this code and will be returned at the end.
        float insertedMana = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toInsert = requestedInsertion - insertedMana;

            // implicit NaN check (as NaN < 0 = false)
            if (toInsert < 0) throw new IllegalStateException("toInsert should never be < 0. It is " + toInsert + "!");
            if (toInsert == 0) break;

            insertedMana += manaPool.insertMana(world, toInsert, context);
        }

        return insertedMana;
    }

    // Very sane code ;v; Update: It got better
    @Override
    public float extractMana(ServerWorld world, float requestedExtraction, TransactionContext context) {
        if (requestedExtraction < 0) throw new IllegalArgumentException("Extraction amount is negative.");

        // This local is going to get modified throughout this code and will be returned at the end.
        float extractedMana = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toExtract = requestedExtraction - extractedMana;

            // implicit NaN check (as NaN < 0 = false)
            if (toExtract < 0) throw new IllegalStateException("toExtract should never be < 0. It is " + toExtract + "!");
            if (toExtract == 0) break;

            extractedMana += manaPool.extractMana(world, toExtract, context);
        }

        return extractedMana;
    }
    //In the case I ever decide to make stateful ManaPools
    /**@Override
    public void writeNbt(NbtCompound nbt) {
        NbtList manaPoolsNbt = new NbtList();
        manaPools.forEach((entry, manaPool) ->  {
            entry.getKey().map(RegistryKey::getValue).ifPresent(id -> {
                NbtCompound poolNbt = new NbtCompound();
                poolNbt.putString("id", id.toString());

                NbtCompound data = new NbtCompound();
                manaPool.writeNbt(data);
                poolNbt.put("data", data);

                manaPoolsNbt.add(poolNbt);
            });
        });
        nbt.put("manaPoolsData", manaPoolsNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        var nbtList = nbt.getList("manaPoolsData", NbtElement.COMPOUND_TYPE);
        nbtList.forEach(nbtElement -> {
            var com = ((NbtCompound) nbtElement);
            var id = Identifier.tryParse(com.getString("id"));
            EntityManaPoolRegistererImpl.REGISTRY.getEntry(id).flatMap(entry -> Optional.ofNullable(manaPools.get(entry))).ifPresent(manaPool -> {
                manaPool.readNbt(com.getCompound("data"));
            });
        });
    }**/

    public void copyFrom(ServerManaManager manaManager) {
        this.manaPools = manaManager.manaPools;
    }
}
