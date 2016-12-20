package main.java.zagar.view.cells;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Created by xakep666 on 18.12.16.
 */
public final class Food extends Cell {
    public Food(@NotNull Point2D coordinate,
                @NotNull Color color,
                double mass,
                double radius) {
        super(coordinate, color, mass, radius);
    }

    @Override
    protected void addShape(@NotNull Graphics2D g, @NotNull Point2D centerCoordinate) {
        Path2D polygon = new Path2D.Double();
        polygon.moveTo(
                centerCoordinate.getX() + 10,
                centerCoordinate.getY()
        );
        for (int i = 1; i < 6; i++) {
            polygon.lineTo(
                    centerCoordinate.getX() + 10 * Math.cos(i * 2 * Math.PI / 6),
                    centerCoordinate.getY() + 10 * Math.sin(i * 2 * Math.PI / 6)
            );
        }
        g.fill(polygon);
    }
}
