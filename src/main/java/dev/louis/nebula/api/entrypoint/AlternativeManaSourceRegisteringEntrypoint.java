package dev.louis.nebula.api.entrypoint;

import dev.louis.nebula.entrypoint.AlternativeManaSourceRegistererImpl;

@FunctionalInterface
public interface AlternativeManaSourceRegisteringEntrypoint {
    void registerAlternativeManaSources(AlternativeManaSourceRegistererImpl registerer);
}
