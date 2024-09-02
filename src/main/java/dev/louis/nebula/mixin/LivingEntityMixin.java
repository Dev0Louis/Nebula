package dev.louis.nebula.mixin;

import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.ManaPoolHolder;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    protected NebulaManaManager manaManager = new NebulaManaManager((LivingEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.readNbt(nbt);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaAndSpellManager(CallbackInfo ci) {
        this.manaManager.tick();
    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public @NotNull ManaPool getManaPool() {
        return this.getManaManager();
    }
}
