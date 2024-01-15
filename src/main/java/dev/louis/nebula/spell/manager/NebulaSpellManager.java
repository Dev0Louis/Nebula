package dev.louis.nebula.spell.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.event.SpellCastCallback;
import dev.louis.nebula.networking.SpellCastC2SPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NebulaSpellManager implements SpellManager {
    protected static final String SPELL_NBT_KEY = "Spell";
    protected static final String SPELLS_NBT_KEY = "Spells";

    protected final Set<SpellType<?>> castableSpells = new HashSet<>();
    protected PlayerEntity player;

    public NebulaSpellManager(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        //TODO: Adapt to real spells.
        /*
        this.tickingSpells.removeIf(tickingSpell -> {
            boolean willBeRemoved = !tickingSpell.shouldContinue();
            if(willBeRemoved) tickingSpell.stop();
            return willBeRemoved;
        });
        for (TickingSpell tickingSpell : this.tickingSpells) {
            tickingSpell.tick();
        }
        */
    }

    @Override
    public boolean learnSpell(SpellType<?> spellType) {
        boolean shouldSync = this.castableSpells.add(spellType);
        if(shouldSync) this.sendSync();
        return true;
    }

    @Override
    public boolean forgetSpell(SpellType<?> spellType) {
        boolean shouldSync = this.castableSpells.remove(spellType);
        if(shouldSync) this.sendSync();
        return true;
    }

    protected Set<SpellType<?>> getCastableSpells() {
        return castableSpells;
    }

    protected void updateCastableSpell(Map<SpellType<?>, Boolean> castableSpells) {
        castableSpells.forEach((spellType, knows) -> {
                if (knows) this.castableSpells.add(spellType);
                else this.castableSpells.remove(spellType);
        });
        this.sendSync();
    }

    @Override
    public void cast(SpellType<?> spellType) {
        this.cast(spellType, this.player.getPos());
    }

    @Override
    public void cast(SpellType<?> spellType, Vec3d pos) {
        this.cast(spellType.create(this.player, pos));
    }

    @Override
    public void cast(Spell spell) {
        if(SpellCastCallback.EVENT.invoker().interact(this.player, spell) != ActionResult.PASS) return;
        if(spell.isCastable()) {
            if(this.isServer()) {
                spell.applyCost();
                spell.cast();
            } else {
                new SpellCastC2SPacket(spell).sendToServer();
                //ClientPlayNetworking.send(SpellCastC2SPacket.getId(), new SpellCastC2SPacket(spell).write());
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if(this.isServer()) {
            //TODO: Adapt to spells.
            //this.tickingSpells.forEach((tickingSpell -> tickingSpell.stop(true)));
        }
        this.castableSpells.clear();
        //TODO: Adapt to spells.
        //this.tickingSpells.clear();
    }

    @Override
    public boolean isCastable(SpellType<?> spellType) {
        return  !spellType.needsLearning() || this.hasLearned(spellType);
    }

    @Override
    public boolean hasLearned(SpellType<?> spellType) {
        return this.castableSpells.contains(spellType);
    }

    @Override
    public boolean sendSync() {
        if(this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            Map<SpellType<?>, Boolean> castableSpells = new HashMap<>();
            Nebula.SPELL_REGISTRY.forEach(spellType -> castableSpells.put(spellType, this.hasLearned(spellType)));
            new UpdateSpellCastabilityS2CPacket(castableSpells).sendToPlayer(serverPlayerEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(this.isServer()) {
            Nebula.LOGGER.error("Called receiveSync on server side!");
            return false;
        }
        UpdateSpellCastabilityS2CPacket packet = UpdateSpellCastabilityS2CPacket.read(buf);
        MinecraftClient.getInstance().executeSync(() -> this.updateCastableSpell(packet.spells()));
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

    public boolean isServer() {
        return !player.getWorld().isClient();
    }
}
