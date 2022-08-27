package org.d1p4k.nebula.knowledge;

import net.minecraft.nbt.NbtList;

public abstract class Knowledge {



    public abstract NbtList writeNbt(NbtList nbtList);
    public abstract void readNbt(NbtList nbtList);

    //public static Object from(NbtList nbtList);






}
