package zagar.network.handlers;

import protocol.CommandThankYou;
import zagar.Game;
import zagar.network.ServerConnectionSocket;
import zagar.util.JSONHelper;

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

