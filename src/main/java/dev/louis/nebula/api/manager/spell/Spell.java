package dev.louis.nebula.api.manager.spell;

import dev.louis.nebula.Nebula;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 * And the location it was cast at.
 */
public abstract class Spell {
    public static final TrackedDataHandler<Optional<Spell>> OPTIONAL_SPELL = new TrackedDataHandler.ImmutableHandler<>() {
        public void write(PacketByteBuf buf, Optional<Spell> optionalSpell) {
            buf.writeBoolean(optionalSpell.isPresent());
            optionalSpell.ifPresent(spell -> {
                buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spell.getType());
                spell.writeBuf(buf);
            });
        }

        public Optional<Spell> read(PacketByteBuf buf) {
            if(!buf.readBoolean()) return Optional.empty();
            SpellType<?> spellType = buf.readRegistryValue(Nebula.SPELL_REGISTRY);
            if(spellType == null) throw new IllegalStateException("Spell type not found in registry");
            var spell = spellType.create();
            spell.readBuf(buf);
            return Optional.of(spell);
        }
    };


    private final SpellType<?> spellType;
    private PlayerEntity caster;

    protected int spellAge = 0;
    protected boolean stopped;
    protected boolean wasInterrupted;

    public Spell(SpellType<?> spellType) {
        this.spellType = spellType;
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

    public SpellType<? extends Spell> getType() {
        return this.spellType;
    }

    public int getMaxAge() {
        return 20 * 3;
    }

    protected boolean shouldContinue() {
        return this.spellAge >= this.getMaxAge();
    }

    public void setCaster(PlayerEntity caster) {
        this.caster = caster;
    }

    public final boolean shouldStop() {
        return this.stopped && !shouldContinue();
    }

    public void stop() {
        this.stopped = true;
    }

    public void interrupt() {
        this.wasInterrupted = true;
        stop();
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
        return this.getCaster().getWorld().isClient();
    }

    public boolean wasInterrupted() {
        return this.wasInterrupted;
    }

    public boolean wasStopped() {
        return this.stopped;
    }

    /**
     * Read additional casting data about the spell from the buf.
     * @param buf The buf to be read from.
     * @return The buf after being read from.
     */
    public PacketByteBuf readBuf(PacketByteBuf buf) {
        return buf;
    }

    /**
     * Write additional casting data about the spell to the buf.
     * @param buf The buf to be written to.
     * @return The buf after being written to.
     */
    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        return buf;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getID().toString() + "]";
    }
}
