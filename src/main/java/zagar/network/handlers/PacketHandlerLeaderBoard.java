package main.java.zagar.network.handlers;

import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandLeaderBoard;
import main.java.zagar.Game;
import main.java.zagar.util.JSONDeserializationException;
import main.java.zagar.util.JSONHelper;

public class PacketHandlerLeaderBoard {
  public PacketHandlerLeaderBoard(@NotNull String json) {
    CommandLeaderBoard commandLeaderBoard;
    try {
      commandLeaderBoard = JSONHelper.fromJSON(json, CommandLeaderBoard.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
      return;
    }
    Game.leaderBoard = commandLeaderBoard.getLeaderBoard();
  }
}
