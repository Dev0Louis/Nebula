package dev.louis.nebula.api.event;

import dev.louis.nebula.api.mana.EntityInsertionContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface EntityManaInsertionCallback {
    Event<EntityManaInsertionCallback.Before> BEFORE = EventFactory.createArrayBacked(EntityManaInsertionCallback.Before.class, (listeners) -> (context) -> {
                for (EntityManaInsertionCallback.Before event : listeners) {
                    var passed = event.canInsertMana(context);
                    if (!passed) return false;
                }
                return true;
            }
    );

    Event<EntityManaInsertionCallback.After> AFTER = EventFactory.createArrayBacked(EntityManaInsertionCallback.After.class, (listeners) -> (context) -> {
                for (EntityManaInsertionCallback.After event : listeners) {
                    event.onManaInsertion(context);
                }
            }
    );

    interface Before {
        boolean canInsertMana(EntityInsertionContext context);
    }

    interface After {
        void onManaInsertion(EntityInsertionContext context);
    }
}
