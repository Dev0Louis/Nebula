package org.d1p4k.nebula.knowledge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.packet.s2c.KnowledgeS2CPacket;
import org.d1p4k.nebula.registry.NebulaRegistries;

import java.util.HashSet;
import java.util.Set;

public class SpellKnowledge extends Knowledge {
    PlayerEntity player;


    public SpellKnowledge(PlayerEntity player) {
        this.player = player;
    }

    //private List<Identifier> castableSpells = new ArrayList<>();
    private Set<Identifier> castableSpells = new HashSet<>();


    /**
     * @return a copy of the CastableSpells.
     */
    public Set<Identifier> getCastableSpells() {
        return new HashSet<>(castableSpells);
    }


    /**
     * The setCastableSpells function sets the castableSpells field of this object to the given list.
     *
     * @param castableSpells Set the list of spells that the player can cast
     *
     */
    private void setCastableSpells(Set<Identifier> castableSpells) {
        this.castableSpells = castableSpells;
    }
    public void addCastableSpell(Identifier... spells) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            for(Identifier spell : spells) {
                castableSpells.add(spell);
                KnowledgeS2CPacket.send(serverPlayer, spell);
            }
        }
    }
    public void removeCastableSpell(Identifier... spells) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            for(Identifier spell : spells) {
                castableSpells.remove(spell);
                KnowledgeS2CPacket.send(serverPlayer, spell);
            }
        }
    }
    public void copyFrom(ServerPlayerEntity playerToCopyFrom) {
        setCastableSpells(((NebulaPlayer)playerToCopyFrom).getSpellKnowledge().getCastableSpells());
    }

    public boolean isCastable(Identifier spell) {
        return castableSpells.contains(spell);
    }

    /**
     * The writeNbt function writes the castable spells of a player to an NbtList.
     *
     * @param nbtList Store the data that is read from the nbt file
     *
     * @return A NbtList containing the castable spells.
     */
    @Override
    public NbtList writeNbt(NbtList nbtList) {
        //TODO: Refactoring

        NbtCompound nbtCompound;
        NbtCompound childNbtCompound;
        NebulaPlayer nebulaPlayer = ((NebulaPlayer) player);
        Set<Identifier> castableSpells = nebulaPlayer.getSpellKnowledge().getCastableSpells();


        for (Identifier spell : castableSpells) {
            nbtCompound = new NbtCompound();
            childNbtCompound = new NbtCompound();

            childNbtCompound.putString("Spell", spell.toString());
            nbtCompound.remove(spell.getNamespace());
            nbtCompound.put(spell.getNamespace(), childNbtCompound);

            nbtList.add(nbtCompound);
        }
        return nbtList;

    }
    /**
     * The readNbt function reads the NBT data from a compound tag and stores it.
     *
     * @param nbtList Store the spell data in this List
     *
     */
    @Override
    public void readNbt(NbtList nbtList) {
        //TODO: Refactoring

        NbtCompound nbtCompound;
        NbtCompound childNbtCompound;

        //castableSpells.clear();


        for (int x = 0; x < nbtList.size(); ++x) {
            nbtCompound = nbtList.getCompound(x);

            childNbtCompound = nbtCompound.getCompound(nbtCompound.getKeys().stream().toList().get(0));

            Identifier spell = new Identifier(childNbtCompound.getString("Spell"));

            if (NebulaRegistries.SPELLS.containsId(spell)) {
                castableSpells.add(spell);
            }
        }
    }
}
