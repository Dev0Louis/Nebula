package dev.louis.nebula.spell.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.event.SpellUpdateCallback;
import dev.louis.nebula.networking.SynchronizeSpellsS2CPacket;
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
import net.minecraft.util.ActionResult;
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
        return true;
    }

    @Override
    public boolean removeSpell(SpellType<? extends Spell> spellType) {
        castableSpells.remove(spellType);
        return false;
    }

    private void setCastableSpells(Set<SpellType<? extends Spell>> castableSpells) {
        this.castableSpells = castableSpells;
    }

    private Set<SpellType<? extends Spell>> getCastableSpells() {
        return castableSpells;
    }

    private void updateCastableSpell(Map<SpellType<? extends Spell>, Boolean> castableSpells) {
        if(SpellUpdateCallback.EVENT.invoker().interact(player, castableSpells) != ActionResult.PASS)return;
        castableSpells.forEach((spellType, knows) -> {
                if (knows) this.castableSpells.add(spellType);
                else this.castableSpells.remove(spellType);
        });

        if(player instanceof ServerPlayerEntity serverPlayer) {
            var buf = PacketByteBufs.create();
            new SynchronizeSpellsS2CPacket(castableSpells).write(buf);
            ServerPlayNetworking.send(serverPlayer, SynchronizeSpellsS2CPacket.getID(), buf);
        }
    }

    @Override
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            setCastableSpells(getNebulaSpellmanager(NebulaPlayer.access(oldPlayer)).getCastableSpells());
        }
    }

    @Override
    public boolean canCast(SpellType<? extends Spell> spellType) {
        return castableSpells.contains(spellType);
    }

    @Override
    public boolean sendSync() {
        if(!(player instanceof ServerPlayerEntity serverPlayer))return false;
        Map<SpellType<? extends Spell>, Boolean> castableSpells = new HashMap<>();
        var buf = PacketByteBufs.create();
        new SynchronizeSpellsS2CPacket(castableSpells).write(buf);
        ServerPlayNetworking.send(serverPlayer, SynchronizeSpellsS2CPacket.getID(), buf);
        return true;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SynchronizeSpellsS2CPacket packet = SynchronizeSpellsS2CPacket.read(buf);
        MinecraftClient.getInstance().executeSync(() -> getNebulaSpellmanager(NebulaPlayer.access(player)).updateCastableSpell(packet.spells()));
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (SpellType<? extends Spell> spell : getCastableSpells()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Spell", SpellType.getId(spell).toString());

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
