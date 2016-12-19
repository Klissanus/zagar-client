package main.java.zagar.view.cells;

import main.java.zagar.Game;
import main.java.zagar.Main;
import main.java.zagar.view.GameFrame;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

public abstract class Cell {
    @NotNull
    private Point2D coordinate;
    @NotNull
    private Point2D renderCoordinate;
    private double mass;
    private double size;
    private double sizeRender;
    @NotNull
    private Color color;

    Cell(@NotNull Point2D coordinate, @NotNull Color color, double mass) {
        this.coordinate = coordinate;
        this.renderCoordinate = coordinate;
        this.mass = mass;
        this.size = 10 * Math.sqrt(this.mass / Math.PI); //from server
        this.sizeRender = this.size;
        this.color = color;
    }

    public static Color generateColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

    public void tick() {
        renderCoordinate = new Point2D.Double(
                renderCoordinate.getX() - (renderCoordinate.getX() - coordinate.getX()) / 5f,
                renderCoordinate.getY() - (renderCoordinate.getY() - coordinate.getY()) / 5f
        );
        sizeRender -= (sizeRender - size) / 9f;
    }

    public void render(@NotNull Graphics2D g, double scale) {
        GameFrame frame = Main.getFrame();
        g.setColor(color);
        if (!Game.getPlayers().isEmpty()) {
            int size = (int) ((this.sizeRender * 2.0 * scale) * Game.zoom);

            float avgX = 0;
            float avgY = 0;

            for (Cell c : Game.getPlayers()) {
                if (c != null) {
                    avgX += c.renderCoordinate.getX();
                    avgY += c.renderCoordinate.getY();
                }
            }

            avgX /= Game.getPlayers().size();
            avgY /= Game.getPlayers().size();

            double x = ((renderCoordinate.getX() - avgX) * Game.zoom) + frame.getSize().width / 2 - size / 2;
            double y = ((renderCoordinate.getY() - avgY) * Game.zoom) + frame.getSize().height / 2 - size / 2;

            addShape(g, new Point2D.Double(x, y));
        }
    }

    @NotNull
    public Point2D getRenderCoordinate() {
        return renderCoordinate;
    }

    protected void addShape(@NotNull Graphics2D g, @NotNull Point2D centerCoordinate) {
        Ellipse2D figure = new Ellipse2D.Double(centerCoordinate.getX(), centerCoordinate.getY(), getSize(), getSize());
        g.fill(figure);
    }

    @NotNull
    public Point2D getCoordinate() {
        return coordinate;
    }

    public double getMass() {
        return mass;
    }

    public double getSize() {
        return size;
    }

    @NotNull
    public Color getColor() {
        return color;
    }
}