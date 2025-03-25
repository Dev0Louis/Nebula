package dev.louis.nebula.spell.source;

import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.api.mana.storage.ManaStorageHolder;
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

    public EntitySpellSource(ServerWorld world, E entity, Vec3d pos, boolean populateDefaultComp) {
        this(world, entity, pos, BlockPos.ofFloored(pos), createCompMap(entity, populateDefaultComp));
        if (entity instanceof ManaStorageHolder holder) {
            setComponent(CastComponents.MANA_SOURCE, (ManaSource) holder.getManaStorage());
        }
    }

    private static <E extends Entity> Map<CastComponent<?>, Object> createCompMap(E entity, boolean populateDefaultComp) {
        if (populateDefaultComp) {
            HashMap<CastComponent<?>, Object> compMap = new HashMap<>(2);
            compMap.put(CastComponents.ROTATION, entity.getRotationVector());
            if (entity instanceof ManaStorageHolder holder) {
                compMap.put(CastComponents.MANA_SOURCE, (ManaSource) holder.getManaStorage());
            }
            return compMap;
        } else {
            return new HashMap<>();
        }
    }

    public EntitySpellSource(ServerWorld world, E entity, Vec3d pos, BlockPos blockPos, Map<CastComponent<?>, Object> customDataMap) {
        this.world = world;
        this.entity = entity;
        this.pos = pos;
        this.blockPos = blockPos;
        this.customDataMap = customDataMap;
    }

    @Override
    public boolean tryCastSpell(Spell<E> spell, Transaction transaction) {
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
        if (this.expectComponent(CastComponents.MANA_SOURCE).extractThaum(amount, context) != amount) {
            throw SpellFumble.manaFumble();
        }
    }

    @Override
    public void expectSpellEffectStart(SpellEffect spellEffect, TransactionContext context) throws SpellFumble {
        if (this.getCaster() instanceof LivingEntity livingEntity) {
            var storage = new SpellEffectWrapper(this.getWorld(), livingEntity);
            if (!storage.startSpellEffect(spellEffect, context)) throw SpellFumble.spellEffectFumble();
        }
    }

    public <Value> Optional<Value> getComponent(CastComponent<Value> component) {
        if (component == null) throw new IllegalArgumentException("component can't be null");
        return Optional.ofNullable((Value) this.customDataMap.get(component));
    }

    public <Value> void setComponent(CastComponent<Value> component, Value value) {
        if (value == null) throw new IllegalArgumentException("data can't be null");
        if (component == null) throw new IllegalArgumentException("component can't be null");
        this.customDataMap.put(component, value);
    }

    @Override
    public <Value> void removeComponent(CastComponent<Value> component) {
        if (component == null) throw new IllegalArgumentException("component can't be null");
        this.customDataMap.remove(component);
    }
}
