package main.java.zagar.network;

import com.google.gson.JsonObject;
import main.java.zagar.Game;
import main.java.zagar.network.handlers.*;
import main.java.zagar.network.packets.PacketAuth;
import main.java.zagar.util.JSONHelper;
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@WebSocket(maxTextMessageSize = 100000)
public class ServerConnectionSocket {
    @NotNull
    private static final Logger log = LogManager.getLogger(ServerConnectionSocket.class);
    @NotNull
    private static final Map<String, PacketHandler> handleMap = new HashMap<>();
    @NotNull
    private static final PacketHandlerReplicate handlerReplicate = new PacketHandlerReplicate();
    static {
        handleMap.put(CommandLeaderBoard.NAME, new PacketHandlerLeaderBoard());
        handleMap.put(CommandAuthFail.NAME, new PacketHandlerAuthFail());
        handleMap.put(CommandAuthOk.NAME, new PacketHandlerAuthOk());
    }

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

    @OnWebSocketMessage
    public void onBytePacket(@NotNull byte[] buf, int offset, int length) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
            GZIPInputStream gis = new GZIPInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            gis.close();
            bis.close();
            handlerReplicate.handle(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(@NotNull String msg) {
        JsonObject json = JSONHelper.getJSONObject(msg);
        try {
            String name = json.get("command").getAsString();
            handleMap.get(name).handle(msg);
        } catch (Exception e) {
            log.warn("Command error in received packet: " + e);
        }
    }
}
