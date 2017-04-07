package com.corazza.fosco.lumenGame.geometry;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.gameObjects.Lumen;

import java.util.List;

public abstract class Line extends SchemeLayoutDrawable {

    private boolean showLength = true;
    protected float textOpacity;
    private boolean fixed = false;

    public abstract Radical  length();
    public abstract boolean startsAt(Dot point);
    public abstract List<Dot> getSplitPoints(Line line);
    public abstract boolean contains(Dot point);
    public abstract Animation getLumenAnimation(Lumen lumen);
    public abstract void onAnimationEnd(Lumen lumen);
    public abstract Line lineCreate(int action, Dot normPoint, View schemeLayout);


    public String lengthString(){ return length().equals(Radical.One)? "" : length().toString(showLength); }

    public void setShowLength(boolean showLength) {
        this.showLength = showLength;
    }

    public boolean isShowLength() {
        return showLength;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean isFixed() {
        return fixed;
    }

    public enum Direction {
        GAMMATHETA, THETAGAMMA, NOT_SPECIFIED
    }

    private DrawingSettings drawingSettings = new DrawingSettings();

    public DrawingSettings getDrawingSettings() {
        return drawingSettings;
    }
    public void setDrawingSettings(DrawingSettings drawingSettings) {
        this.drawingSettings = drawingSettings;
    }

    public static DrawingSettings getNewDrawingSettings(boolean showString, boolean showStrike, int color) {
        return new DrawingSettings(showString,showStrike,color);
    }

    public static class DrawingSettings {
        public boolean showString = true;
        public boolean showStrike = true;
        public int color = Color.WHITE;

        private DrawingSettings() {}
        private DrawingSettings(boolean showString, boolean showStrike, int color) {
            this.showString = showString;
            this.showStrike = showStrike;
            this.color = color;
        }
    }

    public abstract Direction suggestDirection(Lumen lumen);
    public abstract PixelDot getFirst(Direction d);
    public abstract PixelDot getLast(Direction d);
}
