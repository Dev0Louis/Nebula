package org.d1p4k.nebula.knowledge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.packet.s2c.KnowledgeS2CPacket;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpellKnowledge extends Knowledge {
    ServerPlayerEntity player;


    public SpellKnowledge(ServerPlayerEntity player) {
        this.player = player;
    }

    private List<Identifier> castableSpells = new ArrayList<>();


    /**
     * @return a copy of the CastableSpells.
     */
    public List<Identifier> getCastableSpells() {
        return new ArrayList<>(castableSpells);
    }


    /**
     * The setCastableSpells function sets the castableSpells field of this object to the given list.
     *
     * @param castableSpells Set the list of spells that the player can cast
     *
     */
    private void setCastableSpells(List<Identifier> castableSpells) {
        this.castableSpells = castableSpells;
    }
    public void addCastableSpell(Identifier... spells) {
        for(Identifier spell : spells) {
            castableSpells.add(spell);
            KnowledgeS2CPacket.send(player, spell);
        }
    }
    public void removeCastableSpell(Identifier... spells) {
        for(Identifier spell : spells) {
            castableSpells.remove(spell);
            KnowledgeS2CPacket.send(player, spell);
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
        List<Identifier> castableSpells = nebulaPlayer.getSpellKnowledge().getCastableSpells();

        for (int i = 0; i < castableSpells.size(); i++) {
            nbtCompound = new NbtCompound();
            childNbtCompound = new NbtCompound();
            Identifier spell = castableSpells.get(i);

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
        NebulaPlayer nebulaPlayer = ((NebulaPlayer) player);
        List<Identifier> castableSpells = nebulaPlayer.getSpellKnowledge().getCastableSpells();

        //castableSpells.clear();


        for (int x = 0; x < nbtList.size(); ++x) {
            nbtCompound = nbtList.getCompound(x);

            childNbtCompound = nbtCompound.getCompound(nbtCompound.getKeys().stream().toList().get(0));

            Identifier spell = new Identifier(childNbtCompound.getString("Spell"));
            if(Registry.isRegistered(spell)) {
                castableSpells.add(spell);
            }
        }


    }

    public static class Registry {
        private static final List<Identifier> REGISTERED_SPELLS = new ArrayList<>();

        public static List<Identifier> getRegisteredSpells() {
            return REGISTERED_SPELLS;
        }

        /**
         * Add a spell to the registry.
         *
         * @param spell Spell to add.
         */
        public static void add(Identifier spell) {
            REGISTERED_SPELLS.add(spell);
        }

        /**
         * The addAll function adds all the spells in the given collection to this registry.
         *
         * @param spells Spells to add
         */
        public static void addAll(Collection<Identifier> spells) {
            spells.forEach(Registry::add);
        }

        /**
         * The isRegistered function checks if the spell is registered.
         *
         * @param spell The Spell to check if it is registered
         *
         * @return True if the spell is registered, false otherwise.
         */
        public static boolean isRegistered(Identifier spell) {
            AtomicBoolean isRegistered = new AtomicBoolean(false);
            REGISTERED_SPELLS.forEach((identifier) -> {
                if(identifier.equals(spell)) {
                    isRegistered.set(true);
                }
            });
        return isRegistered.get();
        }
    }
}
