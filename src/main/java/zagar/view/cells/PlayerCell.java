package main.java.zagar.view.cells;

import main.java.zagar.Main;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

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
        double x = getRenderCoordinate().getX();
        double y = getRenderCoordinate().getY();
        double radius = getRadius();

        //select maximal string by length
        String mass = "[" + getMass() + "]";
        Font font = calculateFontWidth(radius, name.length() > mass.length() ? name : mass, g);

        FontMetrics fm = g.getFontMetrics(font);

        int nameSize = fm.stringWidth(name);
        outlineString(g, name, (int) (x + radius - nameSize / 2), (int) (y + radius));

        int massSize = fm.stringWidth(mass);
        outlineString(g, mass, (int) (x + radius - massSize / 2), (int) (y + radius + 17));
    }

    @NotNull
    private Font calculateFontWidth(double radius, @NotNull String str, @NotNull Graphics2D g) {
        Font font = new Font(Main.getFrame().getCanvas().fontCells.getAttributes());
        Rectangle2D rectangle2D = g.getFontMetrics().getStringBounds(str, g);
        font = font.deriveFont((float) (font.getSize2D() * radius * 2 / rectangle2D.getWidth()));
        //enable kerning
        Map<TextAttribute, Object> attrs = new HashMap<>();
        attrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        font = font.deriveFont(attrs);
        return font;
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
