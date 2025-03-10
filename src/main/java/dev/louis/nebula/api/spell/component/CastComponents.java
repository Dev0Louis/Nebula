package dev.louis.nebula.api.spell.component;

import dev.louis.nebula.api.mana.source.ManaSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class CastComponents {
    public static CastComponent<ManaSource> MANA_SOURCE = CastComponent.create();
    public static CastComponent<Vec3d>      ROTATION = CastComponent.create();

    public static CastComponent<Entity>     TARGET_ENTITY = CastComponent.create();
    public static CastComponent<Vec3d>      TARGET_POS = CastComponent.create();
}
