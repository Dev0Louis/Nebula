package dev.louis.nebula.mixin;

import com.google.common.collect.ImmutableList;
import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.SpellEffects;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.nebula.constants.NbtConstants;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static dev.louis.nebula.constants.NbtConstants.*;

@SuppressWarnings({"AddedMixinMembersNamePattern", "UnreachableCode"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder, SpellEffectHolder {
    @Shadow public abstract float getHealth();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    protected NebulaManaManager manaManager;
    @Unique
    protected HashMap<SpellEffect, Integer /* activityTime */> spellEffects = new HashMap<>();


    // We init just after health was set.
    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V", shift = At.Shift.AFTER)
    )
    public void lateManaManagerInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        manaManager = NebulaManaManager.createManaManager((LivingEntity) (Object) this);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);

        this.manaManager.writeNbt(nebulaNbt);

        NbtList spellEffectsNbt = new NbtList();
        spellEffects.forEach((spellEffect, activityTime) -> {
            var spellEffectNbt = new NbtCompound();
            spellEffectNbt.putString(ID, spellEffect.getId().toString());
            spellEffectNbt.putInt(ACTIVITY_TIME, activityTime);
            spellEffectsNbt.add(spellEffectNbt);
        });
        nebulaNbt.put(SPELL_EFFECTS, spellEffectsNbt);

        nbt.put(NbtConstants.NEBULA, nebulaNbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        this.manaManager.readNbt(nebulaNbt);

        var nbtList = nebulaNbt.getList(SPELL_EFFECTS, NbtCompound.END_TYPE);
        var spellEffects = new HashMap<SpellEffect, Integer>(nbtList.size());

        for (NbtElement nbtElement : nbtList) {
            var spellEffectNbt = (NbtCompound) nbtElement;
            var id = Identifier.tryParse(spellEffectNbt.getString(ID));
            SpellEffects.REGISTRY.getEntry(id).ifPresentOrElse(spellEffect -> {
                spellEffects.put(
                        spellEffect.value(),
                        spellEffectNbt.getInt(ACTIVITY_TIME)
                );
            }, () -> {
                Nebula.LOGGER.warn("Spell effect {} wasn't registered! This can happen if you remove or update Mods!", id);
            });

        }

        this.spellEffects = spellEffects;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaManagerAndSpellEffects(CallbackInfo ci) {
        this.manaManager.tick();

        var iterator = spellEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var spellEffect = entry.getKey();
            if (this.getWorld() instanceof ServerWorld serverWorld && !spellEffect.shouldContinue(serverWorld, (LivingEntity) (Object) this)) {
                iterator.remove();
                onSpellEffectStopped(spellEffect);
                continue;
            }
            spellEffect.tick((LivingEntity) (Object) this);
            spellEffects.put(spellEffect, entry.getValue() + 1);
        }
    }


    @Inject(
            method = "drop",
            at = @At("RETURN")
    )
    public void voidManaAtDeath(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        this.getManaManager().setMana(0);
    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public @NotNull ManaPool getManaPool() {
        return this.manaManager;
    }

    @Override
    public boolean startSpellEffect(SpellEffect spellEffect) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if ((this.getWorld() instanceof ClientWorld || spellEffect.canStart((ServerWorld) this.getWorld(), entity))) {
            var currentlyActive = this.spellEffects.put(spellEffect, 0) == null;
            if (currentlyActive) onSpellEffectStopped(spellEffect);
            onSpellEffectStart(spellEffect);
            return true;
        }
        return false;
    }

    @Override
    public void stopSpellEffect(SpellEffect spellEffect) {
        removeSpellEffect(spellEffect);
    }

    @Override
    public Collection<SpellEffect> getSpellEffects() {
        return ImmutableList.copyOf(this.spellEffects.keySet());
    }

    // Internal

    @Unique
    protected boolean removeSpellEffect(SpellEffect spellEffect) {
        var tmp = this.spellEffects.remove(spellEffect) != null;
        if (tmp) onSpellEffectStopped(spellEffect);
        return tmp;
    }

    @Unique
    protected void onSpellEffectStart(SpellEffect spellEffect) {
        spellEffect.onActivated((LivingEntity) (Object) this);
        if (((Object) this) instanceof ServerPlayerEntity player) {
            var payload = new StartSpellEffectPayload(player.getId(), spellEffect);
            player.getServerWorld().getChunkManager().sendToNearbyPlayers(this, ServerPlayNetworking.createS2CPacket(payload));
        }
    }

    @Unique
    protected void onSpellEffectStopped(SpellEffect spellEffect) {
        spellEffect.onEnd((LivingEntity) (Object) this);
        if (((Object) this) instanceof ServerPlayerEntity player) {
            var payload = new StopSpellEffectPayload(player.getId(), spellEffect);
            player.getServerWorld().getChunkManager().sendToNearbyPlayers(this, ServerPlayNetworking.createS2CPacket(payload));
        }
    }
}
