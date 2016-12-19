package main.java.zagar;

import main.java.zagar.auth.AuthClient;
import main.java.zagar.network.ServerConnectionSocket;
import main.java.zagar.network.packets.PacketEjectMass;
import main.java.zagar.network.packets.PacketMove;
import main.java.zagar.util.Reporter;
import main.java.zagar.view.GameFrame;
import main.java.zagar.view.cells.Cell;
import main.java.zagar.view.cells.PlayerCell;
import main.java.zagar.view.inputforms.HostInputForm;
import main.java.zagar.view.inputforms.LoginPasswordInputForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static main.java.zagar.GameConstants.*;

public class Game {
    @NotNull
    private static final Logger log = LogManager.getLogger(Game.class);
    @NotNull
    public static String[] leaderBoard = new String[10];
    public static double maxSizeX, maxSizeY, minSizeX, minSizeY;
    public static float zoom;
    public static int score;
    @NotNull
    public static ServerConnectionSocket socket;
    @NotNull
    public static String serverToken;
    @NotNull
    public static String login;
    @NotNull
    public static HashMap<Integer, String> cellNames = new HashMap<>();
    public static long fps = 60;
    public static boolean rapidEject;
    @NotNull
    public static GameState state = GameState.NOT_AUTHORIZED;
    @NotNull
    private static List<Cell> cells = new CopyOnWriteArrayList<>();
    @NotNull
    private static volatile List<Cell> bufCells = new CopyOnWriteArrayList<>();
    @NotNull
    public String gameServerUrl;
    @NotNull
    public AuthClient authClient;
    private int sortTimer;

    public Game() {
        HostInputForm gameServerUrlInput =
                new HostInputForm("game server", DEFAULT_GAME_SERVER_HOST, DEFAULT_GAME_SERVER_PORT);
        if (!gameServerUrlInput.showForm()) {
            System.exit(0);
        }
        this.gameServerUrl = "ws://" + gameServerUrlInput.getHost() + ":" + gameServerUrlInput.getPort();
        HostInputForm authServerUrlInput =
                new HostInputForm("authentication server", DEFAULT_ACCOUNT_SERVER_HOST, DEFAULT_ACCOUNT_SERVER_PORT);
        if (!authServerUrlInput.showForm()) {
            System.exit(0);
        }
        authClient = new AuthClient(authServerUrlInput.getHost(), authServerUrlInput.getPort());
        authenticate();

        final WebSocketClient client = new WebSocketClient();
        socket = new ServerConnectionSocket();
        new Thread(() -> {
            try {
                client.start();
                URI serverURI = new URI(gameServerUrl + "/clientConnection");
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                request.setHeader("Origin", "zagar.io");
                client.connect(socket, serverURI, request);
                log.info("Trying to connect <" + gameServerUrl + ">");
                socket.awaitClose(7, TimeUnit.DAYS);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }).start();
    }

    public static void sortCells() {
        cells.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return Double.compare(o1.getSize(), o2.getSize());
        });
    }

    public static void updateBuffer(@NotNull List<Cell> cells) {
        bufCells = cells;
    }

    @NotNull
    public static List<Cell> getCells() {
        return cells;
    }

    public static List<PlayerCell> getPlayers() {
        return cells.stream()
                .filter(PlayerCell.class::isInstance)
                .map(PlayerCell.class::cast)
                .collect(Collectors.toList());
    }

    private void authenticate() {
        while (serverToken == null) {
            AuthOption authOption = chooseAuthOption();
            if (authOption == null) {
                return;
            }
            LoginPasswordInputForm loginForm = new LoginPasswordInputForm();
            if (loginForm.showForm()) {
                Game.login = loginForm.getLogin();
                String password = loginForm.getPassword();
                switch (authOption) {

                    case REGISTER:
                        if (!authClient.register(login, password)) {
                            Reporter.reportFail("Register failed", "Register failed");
                        }//autologin after registration
                        serverToken = authClient.login(Game.login, password);
                        if (serverToken == null) {
                            Reporter.reportWarn("Login failed", "Login failed");
                        }
                        break;

                    case LOGIN:
                        serverToken = authClient.login(Game.login, password);
                        if (serverToken == null) {
                            Reporter.reportWarn("Login failed", "Login failed");
                            break;
                        }
                }
            }
        }
    }

    @Nullable
    private AuthOption chooseAuthOption() {
        Object[] options = {AuthOption.LOGIN, AuthOption.REGISTER};
        int authOption = JOptionPane.showOptionDialog(null,
                "Choose authentication option",
                "Authentication",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (authOption == 0) {
            return AuthOption.LOGIN;
        }
        if (authOption == 1) {
            return AuthOption.REGISTER;
        }
        return null;
    }

    public void tick() throws IOException {
        log.info("[TICK]");
        //read from buffer
        cells = new CopyOnWriteArrayList<>(bufCells);


        if (socket.session != null && getPlayers().size() > 0) {
            int totalScore = 0;
            int totalSize = 0;

            for(Cell c: cells) {
                totalScore += c.getMass();
                totalSize += c.getSize();
            }

            score = totalScore;

            double zoomm = Main.getFrame().getSize().getHeight() / (1024 / Math.pow(Math.min(64.0 / totalSize, 1), 0.4));

            if (zoomm > 1) {
                zoomm = 1;
            }

            if (zoomm == -1) {
                zoomm = zoom;
            }
            zoom += (zoomm - zoom) / 40f;

            if (socket.session.isOpen()) {
                //check if pointer inside window and window in focus
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Rectangle bounds = Main.getFrame().getBounds();
                bounds.setLocation(Main.getFrame().getLocationOnScreen());
                if (!bounds.contains(mousePos) || !Main.getFrame().isFocused()) return;

                GameFrame frame = Main.getFrame();

                double x = (mousePos.getX() - (frame.getX() + frame.getWidth() / 2)) / zoom;
                double y = (mousePos.getY() - (frame.getY() + frame.getHeight() / 2)) / zoom;


                double size = getPlayers().stream().map(Cell::getSize).max(Double::compare).orElse(1.0);
                double dx = x / Math.max(Math.abs(x)/10f, size);
                double dy = y / Math.max(Math.abs(y)/10f, size);

                new PacketMove((float)dx, (float)dy).write(socket.session);

                if (rapidEject) {
                    new PacketEjectMass().write();
                }
            }
        }

        cells.forEach(Cell::tick);

        sortTimer++;

        if (sortTimer > 10) {
            sortCells();
            sortTimer = 0;
        }
        //socket.session.getRemote().sendPing(ByteBuffer.allocate(48));
    }

    private enum AuthOption {
        REGISTER, LOGIN
    }

    public enum GameState {
        NOT_AUTHORIZED, AUTHORIZED
    }
}
