package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CreativeInfiniteManaSource implements ManaSource {
    public LivingEntity entity;

    public CreativeInfiniteManaSource(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public float extractMana(float extraction, TransactionContext context) {
        return entity.isInCreativeMode() ? extraction : 0;
    }
}
