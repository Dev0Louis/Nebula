package dev.louis.nebula.api.event;

import dev.louis.nebula.api.mana.ManaSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface AlternativeManaSourcesEvent {
    Event<Creation> CREATION = EventFactory.createArrayBacked(Creation.class, (listeners) -> (entity) -> {
                List<ManaSource> list = new ArrayList<>(listeners.length /* rough estimate on how large it is gonna be.*/);

                for (Creation creation : listeners) {
                    var manaSources = creation.createManaSources(entity);
                    if (manaSources == null) continue;
                    list.addAll(manaSources);
                }


                return list;
            }
    );



    interface Creation {
        @Nullable
        List<ManaSource> createManaSources(LivingEntity entity);
    }

}
