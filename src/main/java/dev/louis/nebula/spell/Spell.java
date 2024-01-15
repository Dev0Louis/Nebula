package dev.louis.nebula.spell;

import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 * And the location it was cast at.
 */
public abstract class Spell {
    private static final AtomicInteger CURRENT_ID = new AtomicInteger();

    private final int id = CURRENT_ID.incrementAndGet();


    private final SpellType<?> spellType;
    private final PlayerEntity caster;



    private final World world;
    private final Vec3d pos;
    private float yaw;
    private float pitch;

    protected int spellAge = 0;
    protected boolean stopped;
    protected boolean wasInterrupted;


    public Spell(SpellType<?> spellType, PlayerEntity caster, World world, Vec3d pos) {
        this.spellType = spellType;
        this.caster = caster;
        this.world = world;
        this.pos = pos;
    }

    /**
     * Is called after {@link Spell#isCastable()} if the return of the method is true.
     * This should not be called manually.
     * Use {@link SpellManager#cast(Spell)} or {@link SpellManager#cast(SpellType)}
     */
    public abstract void cast();

    public void tick() {
        spellAge++;
    }

    public Identifier getID() {
        return this.getType().getId();
    }

    public PlayerEntity getCaster() {
        return this.caster;
    }

    public Vec3d getPos() {
        return pos;
    }

    public SpellType<? extends Spell> getType() {
        return this.spellType;
    }

    protected boolean shouldContinue() {
        return spellAge >= this.getMaxAge();
    }

    public final boolean shouldStop() {
        return stopped && !shouldContinue();
    }

    public void interrupt() {
        wasInterrupted = true;
        stop();
    }

    public void stop() {
        stopped = true;
    }

    public int getMaxAge() {
        return 20 * 3;
    }

    /**
     * If true {@link Spell#applyCost()} and {@link Spell#cast()}  will be called in that order. <br>
     * If false nothing will be called.
     */
    public final boolean isCastable() {
        return this.getType().isCastable(this.caster);
    }

    /**
     * Remove the Cost required by the SpellType.
     */
    public void applyCost() {
        this.getCaster().getManaManager().drainMana(getType().getManaCost());
    }

    public boolean isClient() {
        return world.isClient();
    }

    /**
     * Read additional casting data about the spell from the buf.
     * @param buf The buf to be read from.
     * @return The buf after being read from.
     */
    public PacketByteBuf readCastBuf(PacketByteBuf buf) {
        return buf;
    }

    /**
     * Write additional casting data about the spell to the buf.
     * @param buf The buf to be written to.
     * @return The buf after being written to.
     */
    public PacketByteBuf writeCastBuf(PacketByteBuf buf) {
        return buf;
    }

    /**
     * Read additional response data about the spell from the buf.
     * @param buf The buf to be read from.
     * @return The buf after being read from.
     */
    public PacketByteBuf readResponseBuf(PacketByteBuf buf) {
        return readCastBuf(buf);
    }

    /**
     * Write additional response data about the spell to the buf.
     * @param buf The buf to be written to.
     * @return The buf after being written to.
     */
    public PacketByteBuf writeResponseBuf(PacketByteBuf buf) {
        return writeCastBuf(buf);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getID().toString() + "]";
    }
}
