package dev.louis.nebula.entrypoint;

import dev.louis.nebula.api.entrypoint.AlternativeManaSourceRegisterer;
import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.api.mana.source.factory.EntityManaSourceFactory;
import dev.louis.nebula.util.Phase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@ApiStatus.Internal
public final class AlternativeManaSourceRegistererImpl implements AlternativeManaSourceRegisterer {
    public static AlternativeManaSourceRegistererImpl INSTANCE = new AlternativeManaSourceRegistererImpl();

    private AlternativeManaSourceRegistererImpl() {
    }

    public void registerPre(Identifier id, EntityManaSourceFactory factory) {
        registerManaAlternativeInPhase(id, factory, Phase.PRE);
    }

    public void registerPost(Identifier id, EntityManaSourceFactory factory) {
        registerManaAlternativeInPhase(id, factory, Phase.POST);
    }


    public final HashMap<Identifier, EntityManaSourceFactory> preManaAlternativeFactories = new HashMap<>();
    public final HashMap<Identifier, EntityManaSourceFactory> postManaAlternativeFactories = new HashMap<>();

    public void registerManaAlternativeInPhase(Identifier id, EntityManaSourceFactory factory, Phase phase) {
        var map = mapForPhase(phase);

        var duplicateId = map.containsKey(id);
        if (duplicateId) throw new IllegalStateException("Duplicate identifier in " + phase + "! (" + id + ")");

        map.put(id, factory);
    }

    private HashMap<Identifier, EntityManaSourceFactory> mapForPhase(Phase phase) {
        return switch (phase) {
            case PRE -> preManaAlternativeFactories;
            case POST -> postManaAlternativeFactories;
        };
    }

    public Collection<ManaSource> createManaSourcesFor(LivingEntity entity, Phase phase) {
        return mapForPhase(phase).values().stream().map(factory -> factory.create(entity)).filter(Objects::nonNull).toList();
    }
}
