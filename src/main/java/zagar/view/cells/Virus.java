package main.java.zagar.view.cells;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by xakep666 on 18.12.16.
 */
public class Virus extends Cell {
    public Virus(@NotNull Point2D coordinate, float size) {
        super(coordinate, Color.GREEN, size);
    }
}
