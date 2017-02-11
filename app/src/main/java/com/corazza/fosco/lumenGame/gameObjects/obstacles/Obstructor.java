package com.corazza.fosco.lumenGame.gameObjects.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

/**
 * Created by Simone on 06/07/2016.
 */
public class Obstructor extends Obstacle {

    @Override
    public int getMainColor() {
        return Palette.get().getNega();
    }

    public static int getColor() {
        return Palette.get().getNega();
    }

    @Override
    protected String getPaintPrefix() { return "OBSTRUCTOR";}

    public Obstructor(Dot gamma, Dot theta) {
        super(gamma, theta);
        variance = 0;
    }

    @Override
    protected void drawSingleTerminator(Canvas canvas, Path polygon, float x, float y, float r) {
        canvas.drawCircle(x, y , r, getPaint(MAINPAINT));
    }

}
