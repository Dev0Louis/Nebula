package org.d1p4k.nebula.knowledge;

import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class Knowledge {



    public abstract NbtList writeNbt(NbtList nbtList);
    public abstract Knowledge readNbt(NbtList nbtList);

    //public static Object from(NbtList nbtList);






}
