package dev.louis.nebula.knowledgemanager.player;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.event.SpellKnowledgeUpdateCallback;
import dev.louis.nebula.networking.SynchronizeSpellKnowledgeS2CPacket;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.*;

public class NebulaPlayerSpellKnowledgeManager implements PlayerSpellKnowledgeManager {
    PlayerEntity player;


    public NebulaPlayerSpellKnowledgeManager(PlayerEntity player) {
        this.player = player;
    }

    private Set<SpellType<? extends Spell>> castableSpells = new HashSet<>();


    /**
     * @return a copy of the CastableSpells.
     */
    @Override
    public Set<SpellType<? extends Spell>> getCastableSpells() {
        return castableSpells;
    }

    @Override
    public void tick() {}

    @Override
    public void setCastableSpells(Set<SpellType<? extends Spell>> castableSpells) {
        this.castableSpells = castableSpells;
    }

    @Override
    public void addCastableSpell(SpellType<? extends Spell> spellType) {
        addCastableSpell(List.of(spellType));
    }


    @Override
    public void addCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells) {
        Map<SpellType<? extends Spell>, Boolean> spellMaps = new HashMap<>();
        for (SpellType<? extends Spell> spellType : castableSpells) {
            spellMaps.put(spellType, true);
        }
        updateCastableSpell(spellMaps);
    }

    public void updateCastableSpell(Map<SpellType<? extends Spell>, Boolean> castableSpells) {
        if(SpellKnowledgeUpdateCallback.EVENT.invoker().interact(player, castableSpells) != ActionResult.PASS)return;
        castableSpells.forEach((spellType, knows) -> {
                if (knows) this.castableSpells.add(spellType);
                else this.castableSpells.remove(spellType);
        });

        if(player instanceof ServerPlayerEntity serverPlayer) {
            var buf = PacketByteBufs.create();
            new SynchronizeSpellKnowledgeS2CPacket(castableSpells).write(buf);
            ServerPlayNetworking.send(serverPlayer, SynchronizeSpellKnowledgeS2CPacket.getID(), buf);
        }
    }

    @Override
    public void removeCastableSpell(SpellType<? extends Spell> spellType) {
        removeCastableSpell(List.of(spellType));
    }

    @Override
    public void removeCastableSpell(Iterable<SpellType<? extends Spell>> castableSpells) {
        Map<SpellType<? extends Spell>, Boolean> spellMaps = new HashMap<>();
        for (SpellType<? extends Spell> spellType : castableSpells) {
                spellMaps.put(spellType, false);
        }
        updateCastableSpell(spellMaps);
    }
    @Override
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            setCastableSpells(((NebulaPlayer)oldPlayer).getSpellKnowledge().getCastableSpells());
        }
    }

    @Override
    public boolean doesKnow(SpellType<? extends Spell> spellType) {
        return castableSpells.contains(spellType);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Set<SpellType<? extends Spell>> castableSpells = NebulaPlayer.access(player).getSpellKnowledge().getCastableSpells();

        NbtList nbtList = new NbtList();
        for (SpellType<? extends Spell> spell : castableSpells) {
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
            Optional<SpellType<? extends Spell>> optionalSpellType = SpellType.get(spell);
            optionalSpellType.ifPresent(castableSpells::add);
        }
    }
}
