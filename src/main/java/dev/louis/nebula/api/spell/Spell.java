package dev.louis.nebula.api.spell;

public abstract class Spell<Caster> implements QuickSpell<Caster> {
    public abstract int getId();

}
