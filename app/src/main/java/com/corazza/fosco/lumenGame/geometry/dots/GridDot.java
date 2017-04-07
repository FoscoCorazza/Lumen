package com.corazza.fosco.lumenGame.geometry.dots;



public class GridDot extends Dot {

    public static GridDot Zero = new GridDot(0,0);
    public static GridDot One = new GridDot(1,1);

    public GridDot(int x, int y) {super(x, y);}
    public GridDot(float x, float y) {super(x, y);}
    public GridDot(double x, double y) {super(x, y);}

    @Override
    public GridDot gridDot() {
        return this;
    }

    @Override
    public PixelDot pixelDot() {
        return new PixelDot(x*gridSize, y*gridSize);
    }

    @Override
    public GridDot add(float x, float y) {
        return new GridDot(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
        return "(" +gridX()+", "+gridY()+")";
    }
}
