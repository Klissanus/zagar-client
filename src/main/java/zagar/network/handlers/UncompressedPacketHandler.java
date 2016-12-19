package main.java.zagar.network.handlers;

import org.jetbrains.annotations.NotNull;

/**
 * Created by xakep666 on 20.12.16.
 */
public interface UncompressedPacketHandler {
    void handle(@NotNull String msg);
}
