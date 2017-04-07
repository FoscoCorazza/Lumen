package com.corazza.fosco.lumenGame.geometry.dots;

import android.graphics.Point;

public class PixelDot extends Dot {

    public PixelDot(int x, int y) {super(x, y);}
    public PixelDot(float x, float y) {super(x, y);}
    public PixelDot(double x, double y) {super(x, y);}

    public PixelDot(Point point) {
        super(point.x, point.y);
    }

    @Override
    public GridDot gridDot() {
        return new GridDot(x/gridSize, y/gridSize);
    }

    @Override
    public PixelDot pixelDot() {
        return this;
    }

    @Override
    public PixelDot add(float x, float y) {
        return new PixelDot(this.x + x, this.y + y);
    }

    public void change(int x1, int y1) {
        x = x1;
        y = y1;
    }

    @Override
    public String toString() {
        return "(" +((int)pixelX())+"px, "+((int)pixelY())+"px)";
    }
}
