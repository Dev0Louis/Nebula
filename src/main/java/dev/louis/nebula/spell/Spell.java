package dev.louis.nebula.spell;

import dev.louis.nebula.api.NebulaUser;
import net.minecraft.util.Identifier;

public abstract class Spell<T extends NebulaUser> {
    private final SpellType<? extends Spell<?>> spellType;
    private final T caster;


    public Spell(SpellType<? extends Spell<?>> spellType, T caster) {
        this.spellType = spellType;
        this.caster = caster;
    }

    public abstract void cast();
    public void drainMana() {
        getCaster().getManaManager().drainMana(getType().getManaCost());
    }
    public Identifier getID() {
        return getType().getId();
    }

    public T getCaster() {
        return caster;
    }
    public SpellType<? extends Spell<?>> getType() {
        return spellType;
    }

    public boolean isCastable() {
        return getType().isCastable(caster);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getID().toString() + "]";
    }

}
