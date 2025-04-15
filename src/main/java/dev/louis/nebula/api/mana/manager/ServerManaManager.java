package dev.louis.nebula.api.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.entity.ManaAttachment;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistrarImpl;
import dev.louis.nebula.mana.EntityManaPoolOrderer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class ServerManaManager implements ManaPool, ManaManager {
    private final LivingEntity entity;
    private int knownTruth;
    private Map<RegistryEntry<EntityManaPoolType>, ManaAttachment> manaPools;

    @ApiStatus.Internal
    public ServerManaManager(LivingEntity entity, Map<RegistryEntry<EntityManaPoolType>, ManaAttachment> manaPools, int truth) {
        this.entity = entity;
        this.manaPools = manaPools;
        this.knownTruth = truth;
    }

    @ApiStatus.Internal
    public static ServerManaManager createManaManager(ServerPlayerEntity player) {
        return new ServerManaManager(
                player,
                EntityManaPoolRegistrarImpl.INSTANCE.createManaPool(player),
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
                this.manaPools = EntityManaPoolRegistrarImpl.INSTANCE.createManaPool(entity);
                readNbt(nbt);
            } else {
                this.manaPools = EntityManaPoolRegistrarImpl.INSTANCE.createManaPool(entity);
            }
        }
    }

    public void tick() {


    }

    public Optional<ManaAttachment> getManaPool(EntityManaPoolType type) {
        return getManaPool(EntityManaPoolType.REGISTRY.getEntry(type));
    }

    public Optional<ManaAttachment> getManaPool(RegistryEntry<EntityManaPoolType> entry) {
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
    public long getThaum() {
        ensureState();
        return manaPools.values().stream().mapToLong(ManaPool::getThaum).reduce(0, ServerManaManager::sumSafe);
    }

    @Override
    public long getThaumCapacity() {
        ensureState();
        return manaPools.values().stream().mapToLong(ManaPool::getThaumCapacity).reduce(0, ServerManaManager::sumSafe);
    }

    @Override
    public long insertThaum(long requestedInsertion, TransactionContext context) {
        if (requestedInsertion < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        ensureState();
        // This local is going to get modified throughout this code and will be returned at the end.
        long insertedThaum = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toInsert = requestedInsertion - insertedThaum;

            // implicit NaN check (as NaN < 0 = false)
            if (toInsert < 0) throw new IllegalStateException("toInsert should never be < 0. It is " + toInsert + "!");
            if (toInsert == 0) break;

            insertedThaum += manaPool.insertThaum(toInsert, context);
        }

        return insertedThaum;
    }

    // Very sane code ;v; Update: It got better
    @Override
    public long extractThaum(long requestedExtraction, TransactionContext context) {
        if (requestedExtraction < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        ensureState();
        // This local is going to get modified throughout this code and will be returned at the end.
        long extractedThaum = 0;
        for (ManaPool manaPool : manaPools.values()) {
            var toExtract = requestedExtraction - extractedThaum;

            // implicit NaN check (as NaN < 0 = false)
            if (toExtract < 0) throw new IllegalStateException("toExtract should never be < 0. It is " + toExtract + "!");
            if (toExtract == 0) break;

            extractedThaum += manaPool.extractThaum(toExtract, context);
        }

        return extractedThaum;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        ensureState();
        NbtList nbtList = new NbtList();
        this.manaPools.forEach((entry, manaPool) -> {
            NbtCompound nbt1 = new NbtCompound();
            nbt1.put("type", EntityManaPoolType.REGISTRY.getEntryCodec().encodeStart(NbtOps.INSTANCE, entry).getOrThrow());
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
            var type = EntityManaPoolType.REGISTRY.getEntryCodec().decode(NbtOps.INSTANCE, nbt1.get("type")).getOrThrow().getFirst();
            var manaPool = this.manaPools.get(type);
            if (manaPool == null) {
                Nebula.LOGGER.warn("Didn't find manaPool for type {}", type);
                return;
            }
            manaPool.readNbt(nbt1.getCompound("data"));
        });
    }

    public void copyFrom(ServerManaManager manaManager) {
        this.manaPools = manaManager.manaPools;
    }
}
