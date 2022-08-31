package org.d1p4k.nebula.mana.effect;

import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NebulaEffects {

    private static Potion MANA_INSTANT;
    private static Potion MANA_REGENERATION;
    private static Potion STRONG_MANA_REGENERATION;
    private static InstantStatusEffect MANA_INSTANT_EFFECT = new InstantManaStatusEffect();
    private static StatusEffect MANA_REGENERATION_EFFECT = new ManaRegenerationStatusEffect();


    public static void init() {
        MANA_INSTANT_EFFECT = Registry.register(
                Registry.STATUS_EFFECT,
                new Identifier("nebula", "instant_mana"),
                MANA_INSTANT_EFFECT);
        MANA_INSTANT = register("instant_mana", new Potion("Instant_Mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1)));

        MANA_REGENERATION_EFFECT = Registry.register(
                Registry.STATUS_EFFECT,
                new Identifier("nebula", "mana_regeneration"),
                MANA_REGENERATION_EFFECT
        );
        MANA_REGENERATION = register("mana_regeneration", new Potion("Mana_Regeneration",
                new StatusEffectInstance(MANA_REGENERATION_EFFECT, 10)));
        STRONG_MANA_REGENERATION = register("strong_mana_regeneration", new Potion("Strong_Mana_Regeneration",
                new StatusEffectInstance(MANA_REGENERATION_EFFECT, 10, 1)));

    }

    private static Potion register(String name, Potion potion) {
        return Registry.register(Registry.POTION, name, potion);
    }
}
