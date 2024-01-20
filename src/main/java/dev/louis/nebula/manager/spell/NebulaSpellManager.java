package dev.louis.nebula.manager.spell;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.event.SpellCastCallback;
import dev.louis.nebula.api.manager.spell.Spell;
import dev.louis.nebula.api.manager.spell.SpellManager;
import dev.louis.nebula.api.manager.spell.SpellType;
import dev.louis.nebula.api.networking.SpellCastC2SPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extending this class is okay, but be aware this implementation!
 */
public class NebulaSpellManager implements SpellManager {
    protected static final String SPELL_NBT_KEY = "Spell";
    protected static final String SPELLS_NBT_KEY = "Spells";

    protected final Set<SpellType<?>> castableSpells = new HashSet<>();
    protected final Set<Spell> activeSpells = new HashSet<>();
    protected PlayerEntity player;
    private boolean dirty;

    public NebulaSpellManager(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        this.activeSpells.removeIf(spell -> {
            boolean willBeRemoved = spell.shouldStop();
            if(willBeRemoved) spell.onEnd();
            return willBeRemoved;
        });

        for (Spell tickingSpell : this.activeSpells) {
            tickingSpell.tick();
        }
        if(dirty) {
            sendSync();
        }
    }

    @Override
    public boolean learnSpell(SpellType<?> spellType) {
        boolean shouldSync = this.castableSpells.add(spellType);
        if(shouldSync) this.markDirty();
        return true;
    }

    @Override
    public boolean forgetSpell(SpellType<?> spellType) {
        boolean shouldSync = this.castableSpells.remove(spellType);
        if(shouldSync) this.markDirty();
        return true;
    }

    protected Set<SpellType<?>> getCastableSpells() {
        return castableSpells;
    }

    @Override
    public void cast(SpellType<?> spellType) {
        var spell = spellType.create();
        spell.setCaster(this.player);
        this.cast(spell);
    }

    @Override
    public void cast(Spell spell) {
        this.ensurePlayerEqualsCaster(spell);
        if(SpellCastCallback.EVENT.invoker().interact(this.player, spell) != ActionResult.PASS) return;
        if(spell.isCastable()) {
            if(this.isServer()) {
                spell.applyCost();
                spell.cast();
            } else {
                ClientPlayNetworking.send(new SpellCastC2SPacket(spell));
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.activeSpells.forEach(spell -> {
            spell.interrupt();
            spell.onEnd();
        });
        this.activeSpells.clear();
        this.castableSpells.clear();
    }

    @Override
    public boolean isCastable(SpellType<?> spellType) {
        return (!spellType.needsLearning() || this.hasLearned(spellType)) && isSpellTypeActive(spellType);
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isSpellTypeActive(SpellType<?> spellType) {
        return this.activeSpells.stream().anyMatch(spell -> spell.getType().equals(spellType));
    };

    @Override
    public boolean isSpellActive(Spell spell) {
        return this.activeSpells.contains(spell);
    };

    @Override
    public boolean hasLearned(SpellType<?> spellType) {
        return this.castableSpells.contains(spellType);
    }

    @Override
    public boolean sendSync() {
        if(this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            Map<SpellType<?>, Boolean> castableSpells = new HashMap<>();
            Nebula.SPELL_REGISTRY.forEach(spellType -> castableSpells.put(spellType, this.hasLearned(spellType)));
            ServerPlayNetworking.send(serverPlayerEntity, new UpdateSpellCastabilityS2CPacket(castableSpells));
            dirty = false;
            return true;
        }
        return false;
    }


    public static boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UpdateSpellCastabilityS2CPacket packet = UpdateSpellCastabilityS2CPacket.read(buf);
        MinecraftClient.getInstance().executeSync(() -> {
            var spellManager = client.player.getSpellManager();
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
        for (SpellType<?> spell : getCastableSpells()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString(SPELL_NBT_KEY, spell.getId().toString());

            nbtList.add(nbtCompound);
        }
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.put(SPELLS_NBT_KEY, nbtList);
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = (NbtList) nbt.getCompound(Nebula.MOD_ID).get(SPELLS_NBT_KEY);
        if(nbtList == null)return;
        for (int x = 0; x < nbtList.size(); ++x) {
            NbtCompound nbtCompound = nbtList.getCompound(x);
            Identifier spell = new Identifier(nbtCompound.getString(SPELL_NBT_KEY));
            SpellType.get(spell).ifPresent(castableSpells::add);
        }
    }

    @Override
    public SpellManager setPlayer(PlayerEntity player) {
        this.player = player;
        return this;
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
