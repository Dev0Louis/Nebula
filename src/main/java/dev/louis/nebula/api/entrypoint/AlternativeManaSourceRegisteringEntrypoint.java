package dev.louis.nebula.api.entrypoint;

@FunctionalInterface
public interface AlternativeManaSourceRegisteringEntrypoint {
    void registerAlternativeManaSources(AlternativeManaSourceRegisterer registerer);
}
