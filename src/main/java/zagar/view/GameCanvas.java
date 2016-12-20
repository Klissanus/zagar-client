package main.java.zagar.view;

import main.java.zagar.Game;
import main.java.zagar.view.cells.Cell;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class GameCanvas extends JPanel {
    public Font fontCells = new Font("Ubuntu", Font.BOLD, 18);
    private BufferedImage screen;
    private Font font = new Font("Ubuntu", Font.BOLD, 30);
    private Font fontLB = new Font("Ubuntu", Font.BOLD, 25);

    public GameCanvas(@NotNull Dimension size) {
        setSize(size);
        screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        setFont(font);
        setVisible(true);
        addComponentListener(new SizeChangeListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render();
    }

    public void render() {
        Graphics ggg = screen.getGraphics();
        Graphics2D g = ((Graphics2D) ggg);
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(220, 220, 220));

        if (Game.fps < 30) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (!Game.getPlayers().isEmpty()) {

            double avgX = 0;
            double avgY = 0;

            for (Cell c : Game.getPlayers()) {
                if (c != null) {
                    avgX += c.getRenderCoordinate().getX();
                    avgY += c.getRenderCoordinate().getY();
                }
            }

            avgX /= Game.getPlayers().size();
            avgY /= Game.getPlayers().size();

            g.setStroke(new BasicStroke(2));

            drawGrid(g, avgX, avgY);
        }

        g.setFont(fontCells);

        Game.getCells().forEach(cell -> {
            if (cell != null) {
                cell.render(g, 1);
                if (cell.getMass() > 9) {
                    cell.render(g, Math.max(1 - 1f / (cell.getMass() / 10f), 0.87f));
                }
            }
        });

        drawScore(g, Game.score);

        drawLeaderboard(g, Game.leaderBoard);

        g.dispose();

        Graphics gg = this.getGraphics();
        gg.drawImage(screen, 0, 0, null);
        gg.dispose();
    }

    private void drawGrid(@NotNull Graphics2D g, double avgX, double avgY) {
        final int minSizeX = 0;
        final int minSizeY = 0;
        final int maxSizeX = getWidth();
        final int maxSizeY = getHeight();

        for (double i = avgX - (getWidth() / 2) / Game.zoom; i < avgX + (getWidth() / 2) / Game.zoom; i += 100) {
            i = (int) (i / 100) * 100;
            int x = (int) ((i - avgX) * Game.zoom) + getWidth() / 2;
            g.drawLine(x, minSizeY, x, maxSizeY);
        }
        for (double i = avgY - (getHeight() / 2) / Game.zoom; i < avgY + (getHeight() / 2) / Game.zoom; i += 100) {
            i = (int) (i / 100) * 100;
            int y = (int) ((i - avgY) * Game.zoom) + getHeight() / 2;
            g.drawLine(minSizeX, y, maxSizeX, y);
        }
    }

    private void drawScore(@NotNull Graphics2D g, int score) {
        g.setFont(font);

        String scoreString = "Score: " + score;

        g.setColor(new Color(0, 0, 0, 0.5f));

        g.fillRect(getWidth() - 202, 10, 184, 265);
        g.fillRect(7, getHeight() - 85, getStringWidth(g, scoreString) + 26, 47);

        g.setColor(Color.WHITE);

        g.drawString(scoreString, 20, getHeight() - 50);
    }

    private void drawLeaderboard(@NotNull Graphics2D g, @NotNull String[] leaderBoard) {
        int i = 0;

        g.setFont(fontLB);

        g.drawString("Leaderboard", getWidth() - 110 - getStringWidth(g, "Leaderboard") / 2, 40);

        g.setFont(fontCells);

        for (String s : leaderBoard) {
            if (s != null) {
                g.drawString(s, getWidth() - 110 - getStringWidth(g, s) / 2, 40 + 22 * (i + 1));
            }
            i++;
        }
    }

    private int getStringWidth(Graphics2D g, String string) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(g.getFont());

        return fm.stringWidth(string);
    }

    private class SizeChangeListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            screen = new BufferedImage(componentEvent.getComponent().getWidth(),
                    componentEvent.getComponent().getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            render();
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
