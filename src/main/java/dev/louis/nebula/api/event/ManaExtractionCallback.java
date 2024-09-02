package dev.louis.nebula.api.event;

import dev.louis.nebula.api.mana.ExtractionContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ManaExtractionCallback {
    Event<ManaExtractionCallback.Before> BEFORE = EventFactory.createArrayBacked(ManaExtractionCallback.Before.class, (listeners) -> (context) -> {
                for (ManaExtractionCallback.Before event : listeners) {
                    var passed = event.canExtractMana(context);
                    if (!passed) return false;
                }
                return true;
            }
    );

    Event<ManaExtractionCallback.After> AFTER = EventFactory.createArrayBacked(ManaExtractionCallback.After.class, (listeners) -> (context) -> {
                for (ManaExtractionCallback.After event : listeners) {
                    event.onManaExtraction(context);
                }
            }
    );

    interface Before {
        boolean canExtractMana(ExtractionContext context);
    }

    interface After {
        void onManaExtraction(ExtractionContext context);
    }
}
