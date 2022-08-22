package org.d1p4k.nebula.knowledge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellKnowledge extends Knowledge {
    PlayerEntity player;
    public final List<Identifier> castableSpells = new ArrayList<>(){
        {
            add(new Identifier("test", "test"));
        }
    };

    public SpellKnowledge(PlayerEntity player) {
        this.player = player;
    }
    @Override
    public NbtList writeNbt(NbtList nbtList) {

        NbtCompound nbtCompound;

        for (int i = 0; i < castableSpells.size(); i++) {
            nbtCompound = new NbtCompound();
            nbtCompound.putString("Spell", castableSpells.get(i).getNamespace());
            nbtList.add(nbtCompound);
        }
        return nbtList;

    }
    @Override
    public SpellKnowledge readNbt(NbtList nbtList) {
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            boolean isKnown = nbtCompound.getBoolean("isKnown");
            if(isKnown) {
                Identifier spell = new Identifier(nbtCompound.getString("Spell"));

                System.out.println("Spell: " + spell);
                castableSpells.add(spell);
            }
        }
        return null;
    }

    public static class Registry {
        public static List<Identifier> REGISTERED_SPELLS;

    }
}
