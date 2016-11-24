package main.java.zagar.network.handlers;

import protocol.commands.CommandThankYou;
import main.java.zagar.Game;
import main.java.zagar.util.JSONHelper;

import java.io.IOException;

public class PacketHandlerAuthOk {
  public PacketHandlerAuthOk() {
    Game.state = Game.GameState.AUTHORIZED;
    try {
      String json = JSONHelper.toJSON(new CommandThankYou(Game.login));
      Game.socket.session.getRemote().sendString(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
