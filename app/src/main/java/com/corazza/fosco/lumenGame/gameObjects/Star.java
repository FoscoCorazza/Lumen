package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;

/**
 * Created by Simone on 29/07/2016.
 */
public class Star extends Segment {

    private static final String PICKEDPAINT = "PICKEDPAINT";
    private boolean picked = false;

    public static int getMainColor() {
        return Consts.Colors.MATERIAL_BLACK;
    }

    public Star(Dot gamma) {
        super(gamma, gamma);
        getDrawingSettings().showString = false;
        getDrawingSettings().showStrike = false;
    }

    @Override
    protected String getPaintPrefix() {
        return "STAR";
    }

    @Override
    protected void initPaints() {
        Paints.put(getPaintPrefix() + BACKPAINT, Consts.Colors.MATERIAL_LIGHTEST_GREY, 1, Paint.Style.STROKE);
        Paints.put(getPaintPrefix() + MAINPAINT, getMainColor(), 4, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + PICKEDPAINT, Consts.Colors.MATERIAL_GREEN, 4, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + TEXTPAINT, Consts.Colors.WHITE, 16, Consts.detailFont);
    }

    @Override
    protected void drawLineTerminators(Canvas canvas, float r) {
        Paint p = picked ? getPaint(PICKEDPAINT) : getPaint(MAINPAINT);
        if (!picked){
            p.setAlpha(extAlpha(100));
        }

            int x = (int) gamma.pixelX();
            int y = (int) gamma.pixelY();
            canvas.drawCircle(x, y, size/6, p);
            canvas.drawCircle(x, y, size/6, getPaint(BACKPAINT));
            Bulb.drawFilamento(canvas, x, y, size / 6, getPaint(BACKPAINT));

    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public void pick() {
        setPicked(true);
    }
}
