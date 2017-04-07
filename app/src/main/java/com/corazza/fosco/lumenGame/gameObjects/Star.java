package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.animations.AlphaAnimation;
import com.corazza.fosco.lumenGame.animations.SizeAnimation;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;

/**
 * Created by Simone on 29/07/2016.
 */
public class Star extends Segment {

    private static final String PICKEDPAINT = "PICKEDPAINT";
    private boolean picked = false;

    AlphaAnimation αAnimation;
    SizeAnimation sAnimation;

    private static int getMainColor() {
        return Palette.get().getBack(Palette.Gradiation.DARKKK);
    }

    public Star(Dot gamma) {
        super(gamma, gamma);
        getDrawingSettings().showString = false;
        getDrawingSettings().showStrike = false;
        αAnimation = new AlphaAnimation(this, 0, 255, 150);
        sAnimation = new SizeAnimation(this, 1, 1.5f, 150);
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
        canvas.drawCircle(x, y, getRadius(), getPaint(MAINPAINT, 0.5f));
        canvas.drawCircle(x, y, getRadius(), getPaint(PICKEDPAINT, αAnimation.getOpacity()));
        canvas.drawCircle(x, y, getRadius(), getPaint(BACKPAINT));
        Bulb.drawFilamento(canvas, x, y, getRadius(), getPaint(BACKPAINT));

    }

    public int getRadius() {
        return (int) (sAnimation.getSizeMultiplier() * size/6);
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        if(picked) {
            if (!this.picked) {
                αAnimation.start();
            }
            sAnimation.start();
        }

        this.picked = picked;
        if(!picked) αAnimation.revert();
    }

    @Override
    public void update() {
        super.update();
        αAnimation.update();
        sAnimation.update();
    }
}
