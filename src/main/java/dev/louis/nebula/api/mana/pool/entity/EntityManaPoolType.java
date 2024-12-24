package dev.louis.nebula.api.mana.pool.entity;

public record EntityManaPoolType(EntityManaPoolFactory entityManaPoolFactory) {


    public static EntityManaPoolType create(EntityManaPoolFactory entityManaPoolFactory) {
        return new EntityManaPoolType(entityManaPoolFactory);
    }

    /*/**
     * The priority of this entrypoint.
     * Smaller Number = Greater Priority.
     * Bigger Number = Smaller Priority.
     * The greater the Priority the earlier the ManaPool will be inserted into or extracted from.

    public int priority() {
        return this.priority;
    }*/
}
