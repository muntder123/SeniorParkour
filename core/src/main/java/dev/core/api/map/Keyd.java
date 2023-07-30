package dev.core.api.map;

import org.jetbrains.annotations.NotNull;

public interface Keyd<T> {
    @NotNull T getKey();

    void setKey(@NotNull T var1);
}
