package main.java.zagar.view;

import main.java.zagar.Game;
import main.java.zagar.controller.KeyboardListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;

public class GameFrame extends JFrame {
  @NotNull
  private static final Logger log = LogManager.getLogger(GameFrame.class);
  private static final long serialVersionUID = 3637327282806739934L;
  public static double mouseX, mouseY;
  @NotNull
  private static Dimension minSize = new Dimension(800, 600);
  private static long startTime = System.currentTimeMillis();
  private static long frames = 0;
  @NotNull
  private static Dimension size = minSize;
  @NotNull
  public GameCanvas canvas;

  public GameFrame() {
    setSize(minSize);
    setMinimumSize(minSize);
    addKeyListener(new KeyboardListener());
    canvas = new GameCanvas(getSize());
    getContentPane().add(canvas);
    addComponentListener(new SizeChangeListener());
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("· zAgar ·");
    pack();
    setVisible(true);
  }

  @NotNull
  public static Dimension getFrameSize() {
    return size;
  }

  public void render() {
    log.info("[RENDER]");
    log.info("CELLS:\n" + Game.cells.toString());
    log.info("PLAYER CELLS SIZE: " + Game.player.size());
    log.info("LEADERBOARD:\n" + Arrays.toString(Game.leaderBoard));
    Point mouseP = getMouseLocation();
    mouseX = mouseP.getX();
    mouseY = mouseP.getY();
    frames++;
    if (System.currentTimeMillis() - startTime > 1000) {
      if (frames < 10) {
        System.err.println("LAG > There were only " + frames + " frames in " + (System.currentTimeMillis() - startTime) + "ms!!!");
      }
      frames = 0;
      startTime = System.currentTimeMillis();
    }
    canvas.render();
  }

  @NotNull
  private Point getMouseLocation() {
    int x = (MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x);
    int y = (MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y - 24);
    return new Point(x, y);
  }

  private class SizeChangeListener implements ComponentListener {
    @Override
    public void componentResized(ComponentEvent componentEvent) {
      size = componentEvent.getComponent().getSize();
      canvas.setSize(size);
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
  }
}
