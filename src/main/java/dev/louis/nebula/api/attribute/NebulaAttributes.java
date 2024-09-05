package dev.louis.nebula.api.attribute;

import dev.louis.nebula.Nebula;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class NebulaAttributes {
    public static final RegistryEntry<EntityAttribute> GENERIC_MANA_REGENERATION = register(
            "generic.mana_regeneration",
            new ClampedEntityAttribute(
                    "attribute.nebula.name.generic.mana_regeneration",
                    0.005f,
                    0f,
                    2.0f
            ).setTracked(true).setCategory(EntityAttribute.Category.POSITIVE)
    );

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(Nebula.MOD_ID, id), attribute);
    }

    public static void init() {

    }

}
