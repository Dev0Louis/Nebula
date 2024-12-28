package dev.louis.nebula.api.spell.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class SpellEffect {

    private final RegistryKey<SpellEffect> key;

    protected SpellEffect(RegistryKey<SpellEffect> key) {
        this.key = key;
    }

    public abstract void onActivated(LivingEntity target);

    public abstract Action tick(LivingEntity target, int age);

    public abstract void onDeactivated(LivingEntity target);


    public RegistryKey<SpellEffect> getKey() {
        return key;
    }

    public Identifier getId() {
        return this.key.getValue();
    }
}
