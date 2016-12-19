package main.java.zagar.network.handlers;

import org.jetbrains.annotations.NotNull;

/**
 * Created by xakep666 on 08.12.16.
 * <p>
 * Interface for packet handlers
 */
public interface TextPacketHandler {
    void handle(@NotNull String message);
}
