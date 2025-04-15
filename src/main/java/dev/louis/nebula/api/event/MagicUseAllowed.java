package dev.louis.nebula.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface MagicUseAllowed {
    Event<MagicUseAllowed> EVENT = EventFactory.createArrayBacked(MagicUseAllowed.class, (listeners) -> (magicUser, world, pos) -> {
                for (MagicUseAllowed event : listeners) {
                    var disallowed = !event.allowMagic(magicUser, world, pos);
                    if (disallowed) return false;
                }

                return true;
            }
    );
    boolean allowMagic(Object magicUser, ServerWorld world, Vec3d pos);

}
