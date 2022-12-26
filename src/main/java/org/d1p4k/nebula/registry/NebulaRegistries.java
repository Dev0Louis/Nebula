package org.d1p4k.nebula.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.spell.AbstractSpell;

public class NebulaRegistries {
    public static Registry<AbstractSpell> SPELLS;

    static {
        SPELLS = FabricRegistryBuilder.createSimple(AbstractSpell.class,
                new Identifier("nebula", "spells")).buildAndRegister();
    }
    public static void init() {

    }
}
