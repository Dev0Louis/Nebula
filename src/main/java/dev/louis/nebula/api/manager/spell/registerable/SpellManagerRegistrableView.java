package dev.louis.nebula.api.manager.spell.registerable;

import dev.louis.nebula.api.manager.spell.SpellManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface SpellManagerRegistrableView {
        void registerSpellManager(
                SpellManager.Factory<?> manaManagerFactory,
                Identifier spellPacketId,
                Consumer<Identifier> spellPacketRegisterer
        );
}