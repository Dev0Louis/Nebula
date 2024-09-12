package dev.louis.nebula.mixin;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaPoolHolder;
import dev.louis.nebula.api.spell.executor.SpellExecutor;
import dev.louis.nebula.api.spell.executor.SpellExecutorExecutor;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Debug(export = true)
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder, SpellExecutorExecutor {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    protected NebulaManaManager manaManager;
    @Unique
    protected List<SpellExecutor> spellExecutors = new ArrayList<>();

    @Inject(
            method = "<init>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;attributes:Lnet/minecraft/entity/attribute/AttributeContainer;", shift = At.Shift.AFTER)
    )
    public void lateManaManagerInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        manaManager = Nebula.createManaManager((LivingEntity) (Object) this);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManagerAndSpellExecutors(CallbackInfo ci) {
        this.manaManager.tick();
        stopSpellExecutorIf(executor -> !executor.shouldContinue(this.getWorld()));
        for (SpellExecutor spellExecutor : spellExecutors) {
            spellExecutor.tick(this.getWorld());
        }
    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public @NotNull ManaPool getManaPool() {
        return this.getManaManager();
    }

    @Override
    public boolean startSpellExecutor(SpellExecutor executor) {
        executor.onEnable(this.getWorld());
        return spellExecutors.add(executor);
    }

    @Override
    public boolean stopSpellExecutorIf(Predicate<SpellExecutor> stopper) {
        return spellExecutors.removeIf(executor -> {
            boolean remove = stopper.test(executor);
            if (remove) executor.onDisable(this.getWorld());
            return remove;
        });
    }

    @Override
    public Stream<SpellExecutor> streamSpellExecutors() {
        return this.spellExecutors.stream();
    }
}
