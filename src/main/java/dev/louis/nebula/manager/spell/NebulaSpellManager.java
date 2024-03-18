package dev.louis.nebula.manager.spell;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.event.SpellCastCallback;
import dev.louis.nebula.api.manager.spell.SpellManager;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.mixin.ClientPlayerEntityAccessor;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Extending this class is okay, but be aware this implementation!
 */
public class NebulaSpellManager implements SpellManager {
    protected static final String SPELL_NBT_KEY = "Spell";
    protected static final String SPELLS_NBT_KEY = "Spells";

    protected final Set<SpellType<?>> learnedSpells = new HashSet<>();
    //Note: Not yet synced to client.
    protected final Set<Spell> activeSpells = new HashSet<>();
    protected PlayerEntity player;
    private boolean dirty;

    public NebulaSpellManager(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        this.activeSpells.removeIf(spell -> {
            boolean shouldStop = spell.shouldStop();
            if (shouldStop) spell.finish();
            return shouldStop;
        });

        for (Spell spell : this.activeSpells) {
            spell.age++;
            spell.tick();
        }
        if (dirty) this.sendSync();
    }

    @Override
    public Collection<SpellType<?>> getLearnedSpells() {
        return List.copyOf(this.learnedSpells);
    }

    @Override
    public boolean learnSpell(SpellType<?> spellType) {
        boolean shouldSync = this.learnedSpells.add(spellType);
        if (shouldSync) this.markDirty();
        return shouldSync;
    }

    @Override
    public boolean forgetSpell(SpellType<?> spellType) {
        boolean shouldSync = this.learnedSpells.remove(spellType);
        if (shouldSync) this.markDirty();
        return shouldSync;
    }

    @Override
    public boolean cast(SpellType<?> spellType) {
        var spell = spellType.create(this.player);
        return this.cast(spell);
    }

    @Override
    public boolean cast(Spell spell) {
        this.ensurePlayerEqualsCaster(spell);
        this.ensureSpellIsNotAlreadyActive(spell);
        if (SpellCastCallback.EVENT.invoker().interact(this.player, spell) != ActionResult.PASS) return false;
        if (spell.isCastable()) {
            if (this.isServer()) {
                spell.applyCost();
                spell.cast();
                this.activeSpells.add(spell);
            } else {
                ClientPlayNetworking.send(new SpellCastC2SPacket(spell.getType()));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        //We don't clear the spells here because that could cause and ConcurrentModificationException if the player gets killed while the spells ends.
        //Spell#interrupt is designed to cancel the spell no matter what, so it's fine cause the active spells are cleared next tick.
        this.activeSpells.forEach(Spell::interrupt);
        this.learnedSpells.clear();
        this.markDirty();
    }

    @Override
    public boolean isCastable(SpellType<?> spellType) {
        return this.player.isAlive() && player.getManaManager().hasEnoughMana(spellType) && (!spellType.needsLearning() || this.hasLearned(spellType)) && (spellType.allowsParallelCasts() || !player.getSpellManager().isSpellTypeActive(spellType));
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public Collection<Spell> getActiveSpells() {
        return List.copyOf(activeSpells);
    }

    @Override
    public boolean isSpellTypeActive(SpellType<?> spellType) {
        return this.activeSpells.stream().anyMatch(spell -> spell.getType().equals(spellType));
    }

    @Override
    public boolean isSpellActive(Spell spell) {
        return this.activeSpells.contains(spell);
    }

    @Override
    public boolean hasLearned(SpellType<?> spellType) {
        return this.learnedSpells.contains(spellType);
    }

    @Override
    public boolean sendSync() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            Map<SpellType<?>, Boolean> castableSpells = new HashMap<>();
            SpellType.REGISTRY.forEach(spellType -> castableSpells.put(spellType, this.hasLearned(spellType)));
            ServerPlayNetworking.send(serverPlayerEntity, new UpdateSpellCastabilityS2CPacket(castableSpells));
            dirty = false;
            return true;
        }
        return false;
    }


    public static boolean receiveSync(UpdateSpellCastabilityS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ((ClientPlayerEntityAccessor)player).getClient().executeSync(() -> {
            SpellManager spellManager = player.getSpellManager();
            packet.spells().forEach((spellType, learned) -> {
                if (learned) spellManager.learnSpell(spellType);
                else spellManager.forgetSpell(spellType);
            });
        });
        return true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (SpellType<?> spellType : this.getLearnedSpells()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString(SPELL_NBT_KEY, spellType.getId().toString());

            nbtList.add(nbtCompound);
        }
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.put(SPELLS_NBT_KEY, nbtList);
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = (NbtList) nbt.getCompound(Nebula.MOD_ID).get(SPELLS_NBT_KEY);
        if (nbtList == null) return;
        for (int x = 0; x < nbtList.size(); ++x) {
            NbtCompound nbtCompound = nbtList.getCompound(x);
            Identifier spell = new Identifier(nbtCompound.getString(SPELL_NBT_KEY));
            SpellType.get(spell).ifPresent(learnedSpells::add);
        }
    }

    @Override
    public SpellManager setPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }

    private void ensureSpellIsNotAlreadyActive(Spell spell) {
        if(this.activeSpells.contains(spell)) {
            throw new IllegalStateException("Spell " + spell.getType() + " is already ticking!");
        }
    }

    protected void ensurePlayerEqualsCaster(Spell spell) {
        if(spell.getCaster() != this.player) {
            throw new IllegalStateException("Spell " + spell.getType() + " was casted by " + spell.getCaster() + " but expected " + this.player);
        }
    }

    public boolean isServer() {
        return !player.getWorld().isClient();
    }
}
