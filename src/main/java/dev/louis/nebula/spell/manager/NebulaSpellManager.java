package dev.louis.nebula.spell.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NebulaSpellManager implements SpellManager {
    PlayerEntity player;


    public NebulaSpellManager(PlayerEntity player) {
        this.player = player;
    }

    private Set<SpellType<? extends Spell>> castableSpells = new HashSet<>();


    @Override
    public void tick() {}

    @Override
    public boolean addSpell(SpellType<? extends Spell> spellType) {
        castableSpells.add(spellType);
        sendSync();
        return true;
    }

    @Override
    public boolean removeSpell(SpellType<? extends Spell> spellType) {
        castableSpells.remove(spellType);
        sendSync();
        return false;
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
    public void cast(PlayerEntity player, SpellType spellType) {
        cast(spellType.create(player));
    }

    @Override
    public void cast(Spell spell) {
        spell.cast();
    }

    @Override
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            setCastableSpells(getNebulaSpellmanager(NebulaPlayer.access(oldPlayer)).getCastableSpells());
        }
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
        if(!(player instanceof ServerPlayerEntity serverPlayer))return false;
        Map<SpellType<? extends Spell>, Boolean> castableSpells = new HashMap<>();
        Nebula.NebulaRegistries.SPELL_TYPE.forEach((spellType -> {
            castableSpells.put(spellType, hasLearned(spellType));
        }));
        var packet = new UpdateSpellCastabilityS2CPacket(castableSpells);
        packet.write(PacketByteBufs.create());
        ServerPlayNetworking.send(serverPlayer, packet);
        return true;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UpdateSpellCastabilityS2CPacket packet = UpdateSpellCastabilityS2CPacket.readBuf(buf);
        System.out.println("PACKET!");
        MinecraftClient.getInstance().executeSync(() -> getNebulaSpellmanager(NebulaPlayer.access(player)).updateCastableSpell(packet.spells()));
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
            NbtCompound nbtCompound = nbtList.getCompound(0);
            Identifier spell = new Identifier(nbtCompound.getString("Spell"));
            SpellType.get(spell).ifPresent(castableSpells::add);
        }
    }

    /**
     * It is safe to do this here because if code runs inside this Spell manager the Spell manager should be this one.
     * @param player The Player you want the Spell Manager from.
     * @return The NebulaSpellManager of that Player.
     */
    private NebulaSpellManager getNebulaSpellmanager(NebulaPlayer player) {
        return (NebulaSpellManager) player.getSpellManager();
    }
}
