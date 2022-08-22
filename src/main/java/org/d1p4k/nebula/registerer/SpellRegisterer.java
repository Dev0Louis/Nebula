package org.d1p4k.nebula.registerer;

import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaSpellRegisterEntrypoint;
import org.d1p4k.nebula.knowledge.SpellKnowledge;

import java.util.List;

public class SpellRegisterer implements NebulaSpellRegisterEntrypoint {

    @Override
    public void registerSpells() {

        SpellKnowledge.Registry.REGISTERED_SPELLS.add(
                new Identifier("nebula", "suicide")
        );
    }
}
