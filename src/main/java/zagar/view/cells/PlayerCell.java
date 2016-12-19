package main.java.zagar.view.cells;

import main.java.zagar.Main;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Created by xakep666 on 18.12.16.
 */
public final class PlayerCell extends Cell {
    @NotNull
    private String name;

    public PlayerCell(@NotNull Point2D coordinate,
                      @NotNull Color color,
                      double mass,
                      double radius,
                      @NotNull String name) {
        super(coordinate, color, mass, radius);
        this.name = name;
    }

    @Override
    protected void addShape(@NotNull Graphics2D g, @NotNull Point2D centerCoordinate) {
        super.addShape(g, centerCoordinate);
        Font font = Main.getFrame().getCanvas().fontCells;
        double x = getRenderCoordinate().getX();
        double y = getRenderCoordinate().getY();
        double size = getSize();

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int fontSize = fm.stringWidth(name);
        outlineString(g, name, (int) (x + size / 2 - fontSize / 2), (int) (y + size / 2));

        String mass = "[" + getMass() + "]";
        int massSize = fm.stringWidth(mass);
        outlineString(g, mass, (int) (x + size / 2 - massSize / 2), (int) (y + size / 2 + 17));
    }

    private void outlineString(Graphics2D g, String string, int x, int y) {
        g.setColor(new Color(70, 70, 70));
        g.drawString(string, x - 1, y);
        g.drawString(string, x + 1, y);
        g.drawString(string, x, y - 1);
        g.drawString(string, x, y + 1);
        g.setColor(new Color(255, 255, 255));
        g.drawString(string, x, y);
    }
}
