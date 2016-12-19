package main.java.zagar.view.cells;

import main.java.zagar.Game;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by xakep666 on 18.12.16.
 */
public final class Virus extends Cell {
    public Virus(@NotNull Point2D coordinate, double mass, double radius) {
        super(coordinate, Color.GREEN, mass, radius);
    }

    @Override
    protected void addShape(@NotNull Graphics2D g, @NotNull Point2D centerCoordinate) {
        Polygon hexagon = new Polygon();
        int size = 50;
        int a = 2 * (100 / 8 + 10);
        a = Math.min(a, 100);
        for (int i = 0; i < a; i++) {
            float pi = 3.14f;
            int spike = 0;
            if (i % 2 == 0) {
                spike = (int) (20 * Math.min(Math.max(1, (100 / 80f)), 8) * Game.zoom);
            }
            hexagon.addPoint((int) (centerCoordinate.getX() + ((size + spike) / 2) * Math.cos(i * 2 * pi / a))
                    + size / 2, (int) (centerCoordinate.getY() + ((size + spike) / 2) * Math.sin(i * 2 * pi / a)) + size / 2);
        }
        g.fillPolygon(hexagon);
    }
}
