package dev.louis.nebula.api.mana.pool.entity;

public record EntityManaPoolType(EntityManaPoolFactory entityManaPoolFactory) {
    public static EntityManaPoolType create(EntityManaPoolFactory entityManaPoolFactory) {
        return new EntityManaPoolType(entityManaPoolFactory);
    }
}
