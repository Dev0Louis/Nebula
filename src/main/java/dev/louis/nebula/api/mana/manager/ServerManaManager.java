package dev.louis.nebula.api.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import dev.louis.nebula.mana.EntityManaPoolOrderer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class ServerManaManager implements ManaPool, ManaManager {
    private final LivingEntity entity;
    private int knownTruth;
    private Map<RegistryEntry<EntityManaPoolType>, EntityManaPool> manaPools;

    private static Map<RegistryEntry<EntityManaPoolType>, EntityManaPool> createMapFromList(List<EntityManaPool> entityManaPools) {
        Map<RegistryEntry<EntityManaPoolType>, EntityManaPool> map = new HashMap<>(entityManaPools.size());
        for (EntityManaPool entityManaPool : entityManaPools) {
            map.put(EntityManaPoolRegistererImpl.REGISTRY.getEntry(entityManaPool.getType()), entityManaPool);
        }
        return map;
    }

    @ApiStatus.Internal
    public ServerManaManager(LivingEntity entity, Map<RegistryEntry<EntityManaPoolType>, EntityManaPool> manaPools, int truth) {
        this.entity = entity;
        this.manaPools = manaPools;
    }

    @ApiStatus.Internal
    public static ServerManaManager createManaManager(LivingEntity entity) {
        return new ServerManaManager(
                entity,
                EntityManaPoolRegistererImpl.INSTANCE.createManaPool(entity),
                EntityManaPoolOrderer.TRUTH
        );
    }

    @ApiStatus.Internal
    public void ensureState() {
        ensureState(true);
    }

    @ApiStatus.Internal
    public void ensureState(boolean transferData) {
        if (knownTruth != EntityManaPoolOrderer.TRUTH) {
            knownTruth = EntityManaPoolOrderer.TRUTH;
            if (transferData) {
                NbtCompound nbt = new NbtCompound();
                writeNbt(nbt);
                this.manaPools = EntityManaPoolRegistererImpl.INSTANCE.createManaPool(entity);
                readNbt(nbt);
            } else {
                this.manaPools = EntityManaPoolRegistererImpl.INSTANCE.createManaPool(entity);
            }
        }
    }

    public void tick() {


    }

    public Optional<EntityManaPool> getManaPool(EntityManaPoolType type) {
        return getManaPool(EntityManaPoolRegistererImpl.REGISTRY.getEntry(type));
    }

    public Optional<EntityManaPool> getManaPool(RegistryEntry<EntityManaPoolType> entry) {
        ensureState();
        return Optional.ofNullable(manaPools.get(entry));
    }

    private static long sumSafe(long a, long b) {
        long result = a + b;
        if (result < Math.min(a, b)) {
            result = Long.MAX_VALUE;
        }
        return result;
    }

    @Override
    public long getMana() {
        ensureState();
        return manaPools.values().stream().mapToLong(ManaPool::getMana).reduce(0, ServerManaManager::sumSafe);
    }

    @Override
    public long getCapacity() {
        ensureState();
        return manaPools.values().stream().mapToLong(ManaPool::getCapacity).reduce(0, ServerManaManager::sumSafe);
    }

    @Override
    public long insertMana(long requestedInsertion, TransactionContext context) {
        if (requestedInsertion < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        ensureState();
        // This local is going to get modified throughout this code and will be returned at the end.
        long insertedMana = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toInsert = requestedInsertion - insertedMana;

            // implicit NaN check (as NaN < 0 = false)
            if (toInsert < 0) throw new IllegalStateException("toInsert should never be < 0. It is " + toInsert + "!");
            if (toInsert == 0) break;

            insertedMana += manaPool.insertMana(toInsert, context);
        }

        return insertedMana;
    }

    // Very sane code ;v; Update: It got better
    @Override
    public long extractMana(long requestedExtraction, TransactionContext context) {
        if (requestedExtraction < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        ensureState();
        // This local is going to get modified throughout this code and will be returned at the end.
        long extractedMana = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toExtract = requestedExtraction - extractedMana;

            // implicit NaN check (as NaN < 0 = false)
            if (toExtract < 0) throw new IllegalStateException("toExtract should never be < 0. It is " + toExtract + "!");
            if (toExtract == 0) break;

            extractedMana += manaPool.extractMana(toExtract, context);
        }

        return extractedMana;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        ensureState();
        NbtList nbtList = new NbtList();
        this.manaPools.forEach((entry, manaPool) -> {
            NbtCompound nbt1 = new NbtCompound();
            nbt1.put("type", EntityManaPoolRegistererImpl.REGISTRY.getEntryCodec().encodeStart(NbtOps.INSTANCE, entry).getOrThrow());
            nbt1.put("data", manaPool.writeNbt(new NbtCompound()));
            nbtList.add(nbt1);
        });
        nbt.put("entityManaPools", nbtList);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        ensureState(false);
        NbtList nbtList = nbt.getList("entityManaPools", NbtElement.COMPOUND_TYPE);
        nbtList.stream().map(nbtElement -> (NbtCompound) nbtElement).forEach((nbt1) -> {
            var type = EntityManaPoolRegistererImpl.REGISTRY.getEntryCodec().decode(NbtOps.INSTANCE, nbt1.get("type")).getOrThrow().getFirst();
            var manaPool = this.manaPools.get(type);
            if (manaPool == null) {
                Nebula.LOGGER.warn("Didn't find manaPool for type {}", type);
                return;
            }
            manaPool.readNbt(nbt1.getCompound("data"));
        });
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
