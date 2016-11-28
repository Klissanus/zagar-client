package main.java.zagar.network;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandAuthFail;
import protocol.commands.CommandAuthOk;
import protocol.commands.CommandLeaderBoard;
import protocol.commands.CommandReplicate;
import main.java.zagar.Game;
import main.java.zagar.network.handlers.PacketHandlerAuthFail;
import main.java.zagar.network.handlers.PacketHandlerAuthOk;
import main.java.zagar.network.handlers.PacketHandlerLeaderBoard;
import main.java.zagar.network.handlers.PacketHandlerReplicate;
import main.java.zagar.network.packets.PacketAuth;
import main.java.zagar.util.JSONHelper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@WebSocket(maxTextMessageSize = 1024)
public class ServerConnectionSocket {
  @NotNull
  private static final Logger log = LogManager.getLogger("<<<");

  @NotNull
  private final CountDownLatch closeLatch;
  @NotNull
  public Session session;

  public ServerConnectionSocket() {
    this.closeLatch = new CountDownLatch(1);
  }

  public boolean awaitClose(int duration, @NotNull TimeUnit unit) throws InterruptedException {
    return this.closeLatch.await(duration, unit);
  }

  @OnWebSocketClose
  public void onClose(int statusCode, @NotNull String reason) {
    log.info("Closed." + statusCode + "<" + reason + ">");
    this.closeLatch.countDown();
  }

  @OnWebSocketConnect
  public void onConnect(@NotNull Session session) throws IOException {
    this.session = session;

    log.info("Connected!");

    new PacketAuth(Game.login, Game.serverToken).write();
  }

  @OnWebSocketMessage
  public void onTextPacket(@NotNull String msg) {
    log.info("Received packet: " + msg);
    if (session.isOpen()) {
      handlePacket(msg);
    }
  }

  public void handlePacket(@NotNull String msg) {
    JsonObject json = JSONHelper.getJSONObject(msg);
    try {
      String name = json.get("command").getAsString();
      switch (name) {
        case CommandLeaderBoard.NAME:
          new PacketHandlerLeaderBoard(msg);
          break;
        case CommandReplicate.NAME:
          new PacketHandlerReplicate(msg);
          break;
        case CommandAuthFail.NAME:
          new PacketHandlerAuthFail(msg);
          break;
        case CommandAuthOk.NAME:
          new PacketHandlerAuthOk();
          break;
      }
    }catch(Exception e){
      log.warn("Command error in received packet: " + e);
    }
  }
}
