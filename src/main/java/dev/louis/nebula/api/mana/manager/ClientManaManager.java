package dev.louis.nebula.api.mana.manager;

import net.minecraft.entity.LivingEntity;

public class ClientManaManager implements ManaManager {
    private long mana;
    private long capacity;

    public ClientManaManager() {

    }

    public static ClientManaManager createManaManager(LivingEntity livingEntity) {
        return new ClientManaManager();
    }

    @Override
    public long getMana() {
        return mana;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public void tick() {

    }

    public void setMana(long mana) {
        this.mana = mana;
    }

    public void copyFrom(ClientManaManager manaManager) {
        this.mana = manaManager.mana;
    }
}
