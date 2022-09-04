package org.d1p4k.nebula.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.d1p4k.nebula.packet.s2c.ManaAmountS2CPacket;

public class Mana {
    private PlayerEntity player;
    public Mana(PlayerEntity p) {
        this.player = p;
    }
    private static final int globalMax = 20;


    private int mana = 0;
    private int max = 20;


    public int get() {
        return mana;
    }


    public void set(int mana, boolean sendToPlayer) {
        this.mana = Math.max(Math.min(mana, max), 0);
        if(sendToPlayer && this.player instanceof ServerPlayerEntity) {
            ManaAmountS2CPacket.send((ServerPlayerEntity) this.player);
        }
    }
    public void set(int mana) {
        set(mana, true);
    }

    public void add(int mana) {
        set(get() + mana);
    }

    public void decrease(int mana) {
        set(get() - mana);
    }

    public boolean decreaseIfEnough(int mana) {
        if(get() - mana < 0) return false;
        decrease(get() - mana);
        return true;
    }

    public int getMax() {
        return max < 0 ? globalMax : max;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public Mana copy() {
        return copy(this.player);
    }

    public Mana copy(PlayerEntity player) {
        Mana manaManager = new Mana(player);
        manaManager.set(this.get());
        manaManager.setMax(this.getMax());
        return manaManager;
    }

    public Mana setPlayer(PlayerEntity p) {
        this.player = p;
        return this;
    }
}
