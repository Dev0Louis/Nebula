package dev.louis.nebula.api.event;

import dev.louis.nebula.api.mana.EntityInsertionContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ManaInsertionCallback {
    Event<ManaInsertionCallback.Before> BEFORE = EventFactory.createArrayBacked(ManaInsertionCallback.Before.class, (listeners) -> (context) -> {
                for (ManaInsertionCallback.Before event : listeners) {
                    var passed = event.canInsertMana(context);
                    if (!passed) return false;
                }
                return true;
            }
    );

    Event<ManaInsertionCallback.After> AFTER = EventFactory.createArrayBacked(ManaInsertionCallback.After.class, (listeners) -> (context) -> {
                for (ManaInsertionCallback.After event : listeners) {
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
