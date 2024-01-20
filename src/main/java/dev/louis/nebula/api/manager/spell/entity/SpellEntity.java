package dev.louis.nebula.api.manager.spell.entity;

import dev.louis.nebula.api.manager.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * An Entity that holds a Spell. Saving is disabled by default.
 * This class can be extended to implement complex Logic for spells.
 * <br><br>
 * The Entity will <b>not</b> be removed when the spell is stopped. <br>
 * This needs to be done by the spell a good place to do this is {@link Spell#onEnd()}
 * <br><br>
 * @param <T> The Spell that is being represented.
 */
public abstract class SpellEntity<T extends Spell> extends Entity {
    protected static final TrackedData<Optional<Spell>> SPELL = DataTracker.registerData(SpellEntity.class, Spell.OPTIONAL_SPELL);

    public SpellEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SPELL, Optional.empty());
    }

    @Override
    public void baseTick() {
        if(shouldDiscardWithSpell() && (this.getSpell().isEmpty() || this.getSpell().get().hasEnded())) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        super.baseTick();
    }

    /**
     * Replaces the current spell with the given spell.
     * This syncs the spell to the client. Regardless of the current state of the TrackedData.
     * @param spell The spell to set. If the spell is null, the spell will be empty.
     */
    public void setSpell(T spell) {
        //We always force set as the Spell is mutable and setting is the only way to force a sync.
        this.dataTracker.set(SPELL, Optional.ofNullable(spell), true);
    }

    /**
     * Syncs the spell to the client.
     * This needs to be done manually because the spell is mutable.
     */
    public void syncSpell() {
        this.dataTracker.set(SPELL, this.dataTracker.get(SPELL), true);
    }

    public Optional<T> getSpell() {
        //noinspection unchecked
        return (Optional<T>) this.dataTracker.get(SPELL);
    }

    public boolean shouldDiscardWithSpell() {
        return true;
    }

    /**
     * SpellEntity does not save to disk by default.
     * Still call super as future Versions might implement logic here.
     */
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    /**
     * SpellEntity does not save to disk by default.
     * Still call super as future Versions might implement logic here.
     */
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    /**
     * SpellEntity does not save to disk by default.
     */
    @Override
    public boolean shouldSave() {
        return false;
    }
}
