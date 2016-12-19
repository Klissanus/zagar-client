package main.java.zagar.view.cells;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by xakep666 on 18.12.16.
 */
public final class Food extends Cell {
    public Food(@NotNull Point2D coordinate, @NotNull Color color, double size) {
        super(coordinate, color, size);
    }

    @Override
    protected void addShape(@NotNull Graphics2D g, @NotNull Point2D centerCoordinate) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            polygon.addPoint(
                    (int) (centerCoordinate.getX() + 10 * Math.cos(i * 2 * Math.PI / 6)),
                    (int) (centerCoordinate.getY() + 10 * Math.sin(i * 2 * Math.PI / 6))
            );
        }
        g.fill(polygon);
    }
}
