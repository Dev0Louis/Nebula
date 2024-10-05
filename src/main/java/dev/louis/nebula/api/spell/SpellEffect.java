package dev.louis.nebula.api.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Experimental
public abstract class SpellEffect {
    private final SpellEffectType<?> type;
    private final RegistryEntry<SpellEffectType<?>> registryEntry;
    protected final LivingEntity target;

    public int age;

    public SpellEffect(SpellEffectType<?> type, LivingEntity target) {
        this.type = type;
        this.registryEntry = SpellEffectType.REGISTRY.getEntry(type);

        this.target = target;
    }

    public abstract void onStart();

    public abstract void tick();

    public abstract void onEnd();

    public boolean shouldContinue() {
        return true;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    public void readNbt(NbtElement nbtElement) {

    }

    public SpellEffectType<?> getType() {
        return type;
    }

    public RegistryEntry<SpellEffectType<?>> getRegistryEntry() {
        return registryEntry;
    }

    public boolean canStart(Collection<SpellEffect> activeSpellEffects) {
        return true;
    }
}
