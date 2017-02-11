package com.corazza.fosco.lumenGame.geometry.dots;
import android.graphics.Point;

import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Utils;

public abstract class Dot {
    public  int gridSize = Consts.baseGridSize ;
    private String tag = "";

    protected float x, y;

    public Dot(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Dot(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Dot(double x, double y){
        this.x = (float) x;
        this.y = (float) y;
    }

    public abstract GridDot gridDot();
    public abstract PixelDot pixelDot();

    public float pixelX() {return pixelDot().x;}
    public float pixelY() {return pixelDot().y;}

    public int gridX() {return (int) gridDot().x;}
    public int gridY() {return (int) gridDot().y;}

    @Override
    public boolean equals(Object that){

        if(that instanceof Dot){
            return pixelX() == ((Dot) that).pixelX() && pixelY() == ((Dot) that).pixelY();
        } else if (that instanceof Point){
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) pixelX();
        result = (int) (31 * result + pixelY());
        return result;
    }

    public PixelDot normalizeToGrid(float factor){
        int newX = (int) pixelX(), newY = (int) pixelY(), g = (int) (gridSize*factor);

        int remainderX = newX % g;
        int remainderY = newY % g;
        if (remainderX != 0) newX = Utils.nearest(newX, newX + g - remainderX, newX - remainderX);
        if (remainderY != 0) newY = Utils.nearest(newY, newY + g - remainderY, newY - remainderY);

        return new PixelDot(newX, newY);
    }

    public Dot half() {
        return new PixelDot(pixelX()/2, pixelY()/2);
    }

    public Dot add(float i) {
        return add(i,i);
    }

    public abstract Dot add(float i, float i1);

    public static Dot min(Dot a, Dot b) {
        return new GridDot(Math.min(a.gridX(),b.gridX()), Math.min(a.gridY(),b.gridY()));
    }

    public static Dot max(Dot a, Dot b) {
        return new GridDot(Math.max(a.gridX(),b.gridX()), Math.max(a.gridY(),b.gridY()));
    }

    public String XMLString() {
        String x = String.valueOf((int) gridDot().x);
        String y = String.valueOf((int) gridDot().y);
        return x + ";" + y;
    }

    public int pixelDistance(Dot dot) {
        return (int) Utils.hypotenuse(pixelX()-dot.pixelX(), pixelY() - dot.pixelY());
    }

}
