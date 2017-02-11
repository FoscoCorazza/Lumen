package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;

/**
 * Created by Simone on 29/07/2016.
 */
public class Star extends Segment {

    private static final String PICKEDPAINT = "PICKEDPAINT";
    private boolean picked = false;
    private int PICK = 78923719;

    private boolean pickingAnimationActive = false;
    private float pickedPaintOpacity = 0;
    private float pickedSizeMultiplier = 1;
    long pickTime = 150;

    private static int getMainColor() {
        return Palette.get().getBack(Palette.Gradiation.DARKKK);
    }

    public Star(Dot gamma) {
        super(gamma, gamma);
        getDrawingSettings().showString = false;
        getDrawingSettings().showStrike = false;
    }

    @Override
    protected String getPaintPrefix() {
        return "STAR" + hashCode()+ "//";
    }

    @Override
    protected void initPaints() {
        Paints.put(getPaintPrefix() + BACKPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), 1, Paint.Style.STROKE);
        Paints.put(getPaintPrefix() + MAINPAINT, getMainColor(), 4, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + PICKEDPAINT, Palette.get().getMain(Palette.Gradiation.NORMAL), 4, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + TEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), 16, Consts.detailFont);
    }

    @Override
    protected void drawLineTerminators(Canvas canvas, int gx, int gy, int tx, int ty, float r) {

        int x = (int) gamma.pixelX();
        int y = (int) gamma.pixelY();
        canvas.drawCircle(x, y, getRadius(), getPaint(MAINPAINT, extAlpha(100)));
        canvas.drawCircle(x, y, getRadius(), getPaint(PICKEDPAINT, pickedPaintOpacity));
        canvas.drawCircle(x, y, getRadius(), getPaint(BACKPAINT));
        Bulb.drawFilamento(canvas, x, y, getRadius(), getPaint(BACKPAINT));

    }

    public int getRadius() {
        return (int) (pickedSizeMultiplier * size/6);
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public void pick() {
        setPicked(true);
        requestPickAnimation();
    }

    public void requestPickAnimation(){

        pickingAnimationActive = true;
        setTimeElapsed(PICK, 0);

    }

    @Override
    public void update() {
        super.update();
        float m = 1.5f;

        if(pickingAnimationActive && picked){
            pickedPaintOpacity   = valueOfNow(PICK,0,1,0,pickTime, AnimType.DEFAULT);
            pickedSizeMultiplier = valueOfNow(PICK,1,m,0,pickTime, AnimType.HALFSINE);
            if(getTimeElapsed(PICK) > pickTime){
                pickingAnimationActive = false;
                pickedPaintOpacity = 1;
            }
        } else if (picked){
            pickedSizeMultiplier = 1;
            pickedPaintOpacity = 1;
        } else {
            pickedSizeMultiplier = 1;
            pickedPaintOpacity = 0;
        }

    }
}
