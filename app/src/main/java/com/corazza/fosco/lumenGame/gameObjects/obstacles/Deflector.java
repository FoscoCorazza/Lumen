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
public class Deflector extends Obstacle {

    @Override
    public int getMainColor() {
        return Palette.get().getDefl();
    }

    @Override
    protected String getPaintPrefix() { return "DEFLECTOR";}

    public Deflector(Dot gamma, Dot theta) {
        super(gamma, theta);
        variance = 0;
    }

    @Override
    protected void drawSingleTerminator(Canvas canvas, Path polygon, float x, float y, float r) {

        canvas.save();
        canvas.rotate(rotation, x,y);

        //Pentagono:
        double size = this.size/4;
        double c1	=  Math.cos((2*Math.PI)/5) * size;
        double c2	=  Math.cos(Math.PI/5)* size;
        double s1	=  Math.sin((2*Math.PI)/5) * size;
        double s2	=  Math.sin((4*Math.PI)/5) * size;

        polygon.reset();
        polygon.moveTo(x, (float) (y + size));
        polygon.lineTo((float) (x - s1), (float) (y + c1));
        polygon.lineTo((float) (x - s2), (float) (y - c2));
        polygon.lineTo((float) (x + s2), (float) (y - c2));
        polygon.lineTo((float) (x + s1), (float) (y + c1));

        canvas.drawPath(polygon, getPaint(MAINPAINT));
        canvas.restore();
    }


}
