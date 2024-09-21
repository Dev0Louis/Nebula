package dev.louis.nebula.api.event;

import dev.louis.nebula.api.mana.EntityExtractionContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface EntityManaExtractionCallback {
    Event<EntityManaExtractionCallback.Before> BEFORE = EventFactory.createArrayBacked(EntityManaExtractionCallback.Before.class, (listeners) -> (context) -> {
                for (EntityManaExtractionCallback.Before event : listeners) {
                    var passed = event.canExtractMana(context);
                    if (!passed) return false;
                }
                return true;
            }
    );

    Event<EntityManaExtractionCallback.After> AFTER = EventFactory.createArrayBacked(EntityManaExtractionCallback.After.class, (listeners) -> (context) -> {
                for (EntityManaExtractionCallback.After event : listeners) {
                    event.onManaExtraction(context);
                }
            }
    );

    interface Before {
        boolean canExtractMana(EntityExtractionContext context);
    }

    interface After {
        void onManaExtraction(EntityExtractionContext context);
    }
}
