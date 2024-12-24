package dev.louis.nebula.api.mana.manager;

import net.minecraft.entity.LivingEntity;

public class ClientManaManager implements ManaManager {
    private float mana;

    public ClientManaManager(float mana) {
        this.mana = mana;
    }

    public static ClientManaManager createManaManager(LivingEntity livingEntity) {
        return new ClientManaManager(Float.NaN);
    }

    @Override
    public float getMana() {
        return mana;
    }

    @Override
    public void tick() {

    }

    public void setMana(float mana) {
        this.mana = mana;
    }
}
