package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.component.CastComponents;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.transaction.SpellEffectWrapper;
import dev.louis.nebula.api.spell.fumble.SpellFumble;
import dev.louis.nebula.api.spell.component.CastComponent;
import dev.louis.nebula.spell.SpellCastHelper;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public class EntitySpellSource<E extends Entity> implements SpellSource<E> {
    protected final E entity;
    private final ServerWorld world;
    private final Vec3d pos;
    private final BlockPos blockPos;
    private final Map<CastComponent<?>, Object> customDataMap;

    public EntitySpellSource(E entity, ServerWorld world, Vec3d pos, BlockPos blockPos) {
        this(entity, world, pos, blockPos, new HashMap<>());
    }

    public EntitySpellSource(E entity, ServerWorld world, Vec3d pos, BlockPos blockPos, HashMap<CastComponent<?>, Object> customDataMap) {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
        this.blockPos = blockPos;
        this.customDataMap = customDataMap;
    }

    @Override
    public boolean castSpell(Spell<E> spell, Transaction transaction) {
        if (!entity.isAlive()) return false;

        return SpellCastHelper.tryCast(this, spell, transaction);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }

    @Override
    public Vec3d getPos() {
        return pos;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public E getCaster() {
        return entity;
    }

    @Override
    public void expectThaum(long amount, TransactionContext context) throws SpellFumble {
        if (this.expectCastComponent(CastComponents.MANA_POOL).extractThaum(amount, context) != amount) {
            throw SpellFumble.manaFumble();
        }
    }

    @Contract
    @Override
    public void expectStartSpellEffect(SpellEffect spellEffect, TransactionContext context) throws SpellFumble {
        if (this.getCaster() instanceof LivingEntity livingEntity) {
            var storage = new SpellEffectWrapper(this.getWorld(), livingEntity);
            if (!storage.startSpellEffect(spellEffect, context)) throw SpellFumble.spellEffectFumble();
        }
    }

    public <Data> Optional<Data> getCastComponent(CastComponent<Data> castComponent) {
        return Optional.ofNullable((Data) this.customDataMap.get(castComponent));
    }

    public <Data> void setCastComponent(CastComponent<Data> castComponent, Data data) {
        this.customDataMap.put(castComponent, data);
    }
}
