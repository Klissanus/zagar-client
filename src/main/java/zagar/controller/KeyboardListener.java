package main.java.zagar.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import main.java.zagar.network.packets.PacketMove;
import main.java.zagar.network.packets.PacketSplit;
import main.java.zagar.network.packets.PacketEjectMass;
import org.jetbrains.annotations.NotNull;
import main.java.zagar.Game;

public class KeyboardListener implements KeyListener {
  @Override
  public void keyPressed(@NotNull KeyEvent e) {
    try {
      if (Game.socket != null && Game.socket.session != null) {
        if (Game.socket.session.isOpen()) {
          if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            new PacketSplit().write();
          }
          if (e.getKeyCode() == KeyEvent.VK_W) {
            new PacketEjectMass().write();
          }
          if (e.getKeyCode() == KeyEvent.VK_T) {
            Game.rapidEject = true;
          }
          if (e.getKeyCode() == KeyEvent.VK_S){
            new PacketMove(1,1).write(Game.socket.session);
          }
        }
      }
    } catch (IOException ioEx) {
      ioEx.printStackTrace();
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
