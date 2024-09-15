package dev.louis.nebula.api.spell;

import net.minecraft.util.math.ChunkPos;

public abstract class Spell {
    public int age;

    public abstract int getId();

    public String getType() {
        return null;
    }

    public boolean hasEnded() {
        return false;
    }

    public void checkEnded() {

    }

    public ChunkPos getChunkPos() {
    }

    public void tick() {

    }
}
