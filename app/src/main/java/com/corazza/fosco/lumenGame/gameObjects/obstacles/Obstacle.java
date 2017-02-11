package com.corazza.fosco.lumenGame.gameObjects.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

/**
 * Created by Simone on 07/07/2016.
 */
public abstract class Obstacle extends Segment {

    protected Path    polygonTheta = new Path();
    protected Path    polygonGamma = new Path();
    protected int     baseSize = Consts.baseGridSize /2;
    protected float   variance = 0.0f;
    protected int     rotation = 0;

    public Obstacle(Dot gamma, Dot theta) {
        super(gamma, theta);
        getDrawingSettings().showString = false;
        getDrawingSettings().showStrike = false;
    }

    public abstract int getMainColor();
    protected abstract void drawSingleTerminator(Canvas canvas, Path polygon, float x, float y, float r);

    @Override
    protected void initPaints() {
        Paints.put(getPaintPrefix() + BACKPAINT, getMainColor(), Consts.lineW+2, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + MAINPAINT, getMainColor(), Consts.lineW, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + TEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(16), Consts.detailFont);
    }

    @Override
    protected void drawLineTerminators(Canvas canvas, int gx, int gy, int tx, int ty, float r) {
        drawSingleTerminator(canvas, polygonGamma, gamma.pixelX(), gamma.pixelY(), Consts.lineTerminatorRadius);
        if(!length().isZero())
            drawSingleTerminator(canvas, polygonTheta, theta.pixelX(), theta.pixelY(), Consts.lineTerminatorRadius);
    }

    @Override
    public void update(){
        updateOpacity();
        rotation = (int) valueOfNow(0, 359, 0, 4000, AnimType.INFINITE);
        size = (int) (baseSize  + baseSize *Math.sin(valueOfNow(0, 359, 0, 1000, AnimType.INFINITE))*variance);
    }
}
