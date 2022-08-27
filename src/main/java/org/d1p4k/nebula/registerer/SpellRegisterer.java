package org.d1p4k.nebula.registerer;

import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaSpellRegisterEntrypoint;

import static org.d1p4k.nebula.knowledge.SpellKnowledge.Registry.add;

public class SpellRegisterer implements NebulaSpellRegisterEntrypoint {

    @Override
    public void registerSpells() {

        add(new Identifier("nebula", "suicide"));
        add(new Identifier("nebula", "cooler"));
        add(new Identifier("addon", "cool"));

    }

}
