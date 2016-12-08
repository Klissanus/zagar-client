package main.java.zagar.network.handlers;

import main.java.zagar.Game;
import main.java.zagar.network.packets.PacketWindowSize;
import main.java.zagar.util.JSONHelper;
import main.java.zagar.view.GameFrame;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandThankYou;

import java.io.IOException;

public class PacketHandlerAuthOk implements PacketHandler {
  public void handle(@NotNull String msg) {
    Game.state = Game.GameState.AUTHORIZED;
    try {
      String json = JSONHelper.toJSON(new CommandThankYou(Game.login));
      Game.socket.session.getRemote().sendString(json);
      //send window size
      new PacketWindowSize(GameFrame.getFrameSize()).write();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
