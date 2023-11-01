package dev.louis.nebula.spell.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.event.SpellCastCallback;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import dev.louis.nebula.spell.TickingSpell;
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

public class NebulaSpellManager implements SpellManager {
    PlayerEntity player;
    private Set<TickingSpell> tickingSpells = new HashSet<>();
    private Set<SpellType<? extends Spell>> castableSpells = new HashSet<>();

    public NebulaSpellManager(PlayerEntity player) {
        this.player = player;
    }


    @Override
    public void tick() {
        this.tickingSpells.removeIf(multiTickSpell -> !multiTickSpell.shouldContinue());
        for (TickingSpell tickingSpell : this.tickingSpells) {
            tickingSpell.tick();
        }
    }

    public Set<TickingSpell> getTickingSpells() {
        return this.tickingSpells;
    }

    public boolean startTickingSpell(TickingSpell tickingSpell) {
        return tickingSpells.add(tickingSpell);
    }

    public boolean stopTickingSpell(TickingSpell tickingSpell) {
        return tickingSpells.add(tickingSpell);
    }

    public void setTickingSpells(Set<TickingSpell> tickingSpells) {
        this.tickingSpells = tickingSpells;
    }

    public boolean isSpellTicking(SpellType<? extends Spell> spellType) {
        return tickingSpells.stream().anyMatch(tickingSpell -> tickingSpell.getType().equals(spellType));
    }

    public boolean isSpellTicking(TickingSpell tickingSpell) {
        return isSpellTicking(tickingSpell.getType());
    }


    @Override
    public boolean learnSpell(SpellType<? extends Spell> spellType) {
        castableSpells.add(spellType);
        sendSync();
        return true;
    }

    @Override
    public boolean forgetSpell(SpellType<? extends Spell> spellType) {
        castableSpells.remove(spellType);
        sendSync();
        return true;
    }

    private void setCastableSpells(Set<SpellType<? extends Spell>> castableSpells) {
        this.castableSpells = castableSpells;
        sendSync();
    }

    private Set<SpellType<? extends Spell>> getCastableSpells() {
        return castableSpells;
    }

    private void updateCastableSpell(Map<SpellType<? extends Spell>, Boolean> castableSpells) {
        castableSpells.forEach((spellType, knows) -> {
                if (knows) this.castableSpells.add(spellType);
                else this.castableSpells.remove(spellType);
        });
        sendSync();
    }

    @Override
    public void cast(PlayerEntity player, SpellType<? extends Spell> spellType) {
        cast(spellType.create(player));
    }

    @Override
    public void cast(Spell spell) {
        if(SpellCastCallback.EVENT.invoker().interact(player, spell) != ActionResult.PASS) return;
        if(spell.isCastable()) {
            spell.drainMana();
            spell.cast();
        }
    }

    @Override
    public void copyFrom(PlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            setCastableSpells(getNebulaSpellmanager(oldPlayer).getCastableSpells());
            setTickingSpells(getNebulaSpellmanager(oldPlayer).getTickingSpells());
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.tickingSpells.forEach((tickingSpell -> tickingSpell.stop(true)));
    }

    public boolean isCastable(SpellType<? extends Spell> spellType) {
        return hasLearned(spellType) && spellType.hasEnoughMana(this.player);
    }

    @Override
    public boolean hasLearned(SpellType<? extends Spell> spellType) {
        return castableSpells.contains(spellType);
    }

    @Override
    public boolean sendSync() {
        if(this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            Map<SpellType<? extends Spell>, Boolean> castableSpells = new HashMap<>();
            Nebula.NebulaRegistries.SPELL_TYPE.forEach(spellType -> castableSpells.put(spellType, hasLearned(spellType)));
            ServerPlayNetworking.send(serverPlayerEntity, new UpdateSpellCastabilityS2CPacket(castableSpells));
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UpdateSpellCastabilityS2CPacket packet = UpdateSpellCastabilityS2CPacket.readBuf(buf);
        MinecraftClient.getInstance().executeSync(() -> this.updateCastableSpell(packet.spells()));
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (SpellType<? extends Spell> spell : getCastableSpells()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Spell", spell.getId().toString());

            nbtList.add(nbtCompound);
        }
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.put("Spells", nbtList);
        nbt.put(Nebula.MOD_ID, nebulaNbt);
        return nbt;

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = (NbtList) nbt.getCompound(Nebula.MOD_ID).get("Spells");
        if(nbtList == null)return;
        for (int x = 0; x < nbtList.size(); ++x) {
            NbtCompound nbtCompound = nbtList.getCompound(x);
            Identifier spell = new Identifier(nbtCompound.getString("Spell"));
            SpellType.get(spell).ifPresent(castableSpells::add);
        }
    }

    /**
     * It is safe to do this here because if code runs inside this Spell manager the Spell manager should be this one.
     * @param player The Player you want the Spell Manager from.
     * @return The NebulaSpellManager of that Player.
     */
    private NebulaSpellManager getNebulaSpellmanager(PlayerEntity player) {
        return (NebulaSpellManager) player.getSpellManager();
    }
}
