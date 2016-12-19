package main.java.zagar.network.handlers;

import main.java.zagar.Game;
import main.java.zagar.util.JSONDeserializationException;
import main.java.zagar.util.JSONHelper;
import main.java.zagar.util.Reporter;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandAuthFail;

public class TextPacketHandlerAuthFail implements TextPacketHandler {
    public void handle(@NotNull String json) {
        CommandAuthFail commandAuthFail;
        try {
            commandAuthFail = JSONHelper.fromJSON(json, CommandAuthFail.class);
        } catch (JSONDeserializationException e) {
            e.printStackTrace();
            return;
        }
        Game.state = Game.GameState.NOT_AUTHORIZED;
        Reporter.reportFail("Token authentication failed", commandAuthFail.getCause());
    }
}
