package org.d1p4k.nebula.mana;

public class Mana {
    private static final int globalMax = 10;


    private int mana = 0;
    private int max = -1;


    public int get() {
        return mana;
    }

    public void set(int mana) {
        this.mana = Math.max(Math.min(mana, max), 0);
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
}
