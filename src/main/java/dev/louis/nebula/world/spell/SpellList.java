package dev.louis.nebula.world.spell;

import dev.louis.nebula.api.spell.Spell;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpellList {
    private Int2ObjectMap<Spell> spells = new Int2ObjectLinkedOpenHashMap<>();
    private Int2ObjectMap<Spell> temp = new Int2ObjectLinkedOpenHashMap<>();
    @Nullable
    private Int2ObjectMap<Spell> iterating;

    /**
     * Ensures that the modified {@code entities} map is not currently iterated.
     * If {@code entities} is iterated, this moves its value to {@code temp} so
     * modification to {@code entities} is safe.
     */
    private void ensureSafe() {
        if (this.iterating == this.spells) {
            this.temp.clear();

            for(Int2ObjectMap.Entry<Spell> entry : Int2ObjectMaps.fastIterable(this.spells)) {
                this.temp.put(entry.getIntKey(), entry.getValue());
            }

            Int2ObjectMap<Spell> spells = this.spells;
            this.spells = this.temp;
            this.temp = spells;
        }
    }

    public void add(Spell spell) {
        this.ensureSafe();
        this.spells.put(spell.getId(), spell);
    }

    public void remove(Spell spell) {
        remove(spell.getId());
    }

    public void remove(int id) {
        this.ensureSafe();
        this.spells.remove(id);
    }

    public boolean has(Spell spell) {
        return has(spell.getId());
    }

    public boolean has(int id) {
        return this.spells.containsKey(id);
    }

    public Spell get(int id) {
        return this.spells.get(id);
    }

    /**
     * Runs an {@code action} on every spell in this storage.
     *
     * <p>If this storage is updated during the iteration, the iteration will
     * not be updated to reflect updated contents. For example, if a spell
     * is added by the {@code action}, the {@code action} won't run on that
     * spell later.
     *
     * @throws UnsupportedOperationException if this is called before an iteration
     * has finished, such as within the {@code action} or from another thread
     */
    public void forEach(Consumer<Spell> action) {
        if (this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterating = this.spells;

            try {
                for(Spell spell : this.spells.values()) {
                    action.accept(spell);
                }
            } finally {
                this.iterating = null;
            }
        }
    }
}
