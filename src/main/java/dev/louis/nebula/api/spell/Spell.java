package dev.louis.nebula.api.spell;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Spell {
    public int age;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();

    private final int id = ID_COUNTER.getAndIncrement();

    private final SpellType<?> type;
    private final World world;

    private Vec3d pos;
    private BlockPos blockPos;
    private ChunkPos chunkPos;


    protected Spell(SpellType<?> type, World world) {
        this.type = type;
        this.world = world;
    }

    public abstract int getId();

    public SpellType<?> getType() {
        return type;
    }

    public boolean hasEnded() {
        return false;
    }

    public void checkEnded() {

    }

    public void tick() {

    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
        this.blockPos = BlockPos.ofFloored(pos);
        this.chunkPos = new ChunkPos(this.blockPos);
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public Vec3d getPos() {
        return pos;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
    }
}
