package main.java.zagar.controller;

import main.java.zagar.Game;
import main.java.zagar.controller.handlers.*;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyboardListener implements KeyListener {

    @NotNull
    static Map<Integer, Event> KeyEventMap = new HashMap<>();

    static {
        KeyEventMap.put(KeyEvent.VK_SPACE, new SplitEvent());
        KeyEventMap.put(KeyEvent.VK_W, new EjectMassEvent());
        KeyEventMap.put(KeyEvent.VK_T, new RapidEjectEvent());
        KeyEventMap.put(KeyEvent.VK_S, new MoveEvent());
    }

    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        if (Game.socket != null && Game.socket.session != null) {
            if (Game.socket.session.isOpen()) {
                if (KeyEventMap.containsKey(e.getKeyCode()))
                    KeyEventMap.get(e.getKeyCode()).handle();
            }
        }
    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {
        if (Game.socket != null && Game.socket.session != null) {
            if (Game.socket.session.isOpen()) {
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    Game.rapidEject = false;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
