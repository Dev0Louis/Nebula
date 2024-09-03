package dev.louis.nebula.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.louis.nebula.api.attribute.NebulaAttributes;
import dev.louis.nebula.duck.DefaultAttributeContainer$BuilderDuck;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultAttributeContainer.Builder.class)
public abstract class DefaultAttributeContainer$BuilderMixin implements DefaultAttributeContainer$BuilderDuck {
    @Shadow @Final private ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances;

    @Shadow public abstract DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> attribute, double baseValue);

    @Shadow private boolean unmodifiable;
    @Unique
    private boolean defineManaOnBuild = false;

    public void nebula$markManaHaving() {
        this.defineManaOnBuild = true;
    }

    @ModifyExpressionValue(
            method = "build",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;instances:Lcom/google/common/collect/ImmutableMap$Builder;")
    )
    public ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> addManaAttributeToAllBuildersHavingHealthAttributes(
            ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> original
    ) {
        this.unmodifiable = false;
        ImmutableMap<RegistryEntry<EntityAttribute>, EntityAttributeInstance> oldMap = instances.buildKeepingLast();
        ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> newBuilder = ImmutableMap.builderWithExpectedSize(oldMap.size() + 1);
        newBuilder.putAll(oldMap);
        if (defineManaOnBuild) {
            if (!oldMap.containsKey(NebulaAttributes.GENERIC_MAX_MANA)) {
                EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(NebulaAttributes.GENERIC_MAX_MANA, attributex -> {
                    if (this.unmodifiable) {
                        throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + NebulaAttributes.GENERIC_MAX_MANA.getIdAsString());
                    }
                });
                entityAttributeInstance.setBaseValue(oldMap.get(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue());
                newBuilder.put(NebulaAttributes.GENERIC_MAX_MANA, entityAttributeInstance);
            }
        }
        this.unmodifiable = true;
        return newBuilder;
    }
}
