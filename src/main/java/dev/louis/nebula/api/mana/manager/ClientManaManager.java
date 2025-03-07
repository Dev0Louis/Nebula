package dev.louis.nebula.api.mana.manager;

import net.minecraft.entity.LivingEntity;

public class ClientManaManager implements ManaManager {
    private long thaum;
    private long capacity;

    public ClientManaManager() {

    }

    public static ClientManaManager createManaManager(LivingEntity livingEntity) {
        return new ClientManaManager();
    }

    @Override
    public long getThaum() {
        return thaum;
    }

    public void setThaumCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getThaumCapacity() {
        return capacity;
    }

    @Override
    public void tick() {

    }

    public void setThaum(long amount) {
        this.thaum = amount;
    }

    public void copyFrom(ClientManaManager manaManager) {
        this.thaum = manaManager.thaum;
    }
}
