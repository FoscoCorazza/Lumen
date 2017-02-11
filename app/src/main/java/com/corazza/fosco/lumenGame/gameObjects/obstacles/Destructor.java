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
public class Destructor extends Obstacle {

    @Override
    public int getMainColor() {
        return Palette.get().getHipo();
    }

    @Override
    protected String getPaintPrefix() { return "DESTRUCTOR";}

    public Destructor(Dot gamma, Dot theta) {
        super(gamma, theta);
        variance = 0;
    }

    @Override
    protected void drawSingleTerminator(Canvas canvas, Path polygon, float x, float y, float r) {

        canvas.save();
        canvas.rotate(rotation, x, y);

        createTriangle(polygon, x, y);

        canvas.drawPath(polygon, getPaint(MAINPAINT));
        canvas.restore();
    }

    private void createStar(Path polygon, int x, int y) {
        int thickness = 1;
        polygon.reset();
        polygon.moveTo(x, y - size / 2);
        polygon.lineTo(x + thickness,   y - thickness);
        polygon.lineTo(x + size/2,      y);
        polygon.lineTo(x + thickness,   y + thickness);
        polygon.lineTo(x,               y + size/2);
        polygon.lineTo(x - thickness,   y + thickness);
        polygon.lineTo(x - size/2,      y);
        polygon.lineTo(x - thickness,   y - thickness);
    }

    private void createTriangle(Path polygon, float x, float y) {

        int s = size/4;
        int l = (int) (size * Math.sqrt(4/3)/4);

        polygon.reset();
        polygon.moveTo(x, y - s);
        polygon.lineTo(x + l,   y + s);
        polygon.lineTo(x - l,   y + s);
    }


}
