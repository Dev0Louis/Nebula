package dev.louis.nebula.api.spell.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Experimental
public abstract class SpellEffect {

    private Identifier id;

    protected SpellEffect(Identifier id) {
        this.id = id;
    }

    public abstract void onActivated(LivingEntity target);

    public abstract void tick(LivingEntity target);

    public abstract void onEnd(LivingEntity target);

    public boolean shouldContinue(ServerWorld serverWorld, LivingEntity livingEntity) {
        return true;
    }

    public boolean canStart(ServerWorld world, Collection<SpellEffect> activeSpellEffects) {
        return true;
    }

    public Identifier getId() {
        return this.id;
    }
}
