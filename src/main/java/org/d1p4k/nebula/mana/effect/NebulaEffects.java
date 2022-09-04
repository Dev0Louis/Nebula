package org.d1p4k.nebula.mana.effect;

import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;

public class NebulaEffects {

    private static Potion MANA_INSTANT;
    private static Potion MANA_REGENERATION;
    private static Potion STRONG_MANA_REGENERATION;
    private static InstantStatusEffect MANA_INSTANT_EFFECT = new InstantManaStatusEffect();
    private static StatusEffect MANA_REGENERATION_EFFECT = new ManaRegenerationStatusEffect();


    public static void init() {
        register(34, "nebula:instant_mana", MANA_INSTANT_EFFECT);

        MANA_INSTANT = register("instant_mana", new Potion("Instant_Mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1, 1)));
        register(35, "nebula:mana_regeneration", MANA_REGENERATION_EFFECT);

        MANA_REGENERATION = register("mana_regeneration", new Potion("Mana_Regeneration",
                new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5)));
        STRONG_MANA_REGENERATION = register("strong_mana_regeneration", new Potion("Strong_Mana_Regeneration",
                new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5, 1)));

    }

    private static Potion register(String name, Potion potion) {
        return Registry.register(Registry.POTION, name, potion);
    }

    private static StatusEffect register(int rawId, String id, StatusEffect entry) {
        return Registry.register(Registry.STATUS_EFFECT, rawId, id, entry);
    }
}
