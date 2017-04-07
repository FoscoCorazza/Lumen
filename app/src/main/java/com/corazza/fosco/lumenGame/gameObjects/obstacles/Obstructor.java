package com.corazza.fosco.lumenGame.gameObjects.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.geometry.Rect;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

/**
 * Created by Simone on 06/07/2016.
 */
public class Obstructor extends Obstacle {

    private static final String EXCLUSIVEPAINT = "EXCLUSIVEPAINT";
    private static final boolean HALFCIRCLEMODE = false;

    public Obstructor(Dot p1, Dot p2, Boolean gammaIncl, Boolean thetaIncl) {
        super(p1, p2);
        variance = 0;
        gammaInclusive = gammaIncl;
        thetaInclusive = thetaIncl;
    }


    public Obstructor(Dot gamma, Dot theta) {
        super(gamma, theta);
        variance = 0;
    }

    @Override
    public int getMainColor() {
        return Palette.get().getNega();
    }

    public static int getColor() {
        return Palette.get().getNega();
    }

    @Override
    protected void initPaints() {
        super.initPaints();
        Paints.put(getPaintPrefix() + EXCLUSIVEPAINT, getColor(), 4, Paint.Style.STROKE);
    }

    @Override
    protected String getPaintPrefix() { return "OBSTRUCTOR";}

    @Override
    public Dot intersecates(Rect rect1){
        Dot d = super.intersecates(rect1);
        if(d != null && !length().isZero()) {
            d = d.pixelDot();
            if ((!gammaInclusive && d.equals(gamma)) || (!thetaInclusive && d.equals(theta))) {
                d = null;
            }
        }
        return d;
    }

    @Override
    protected void drawLine(Canvas canvas, int gx, int gy, int tx, int ty) {
        Paint mainPaint = getPaint(MAINPAINT);
        Paint backPaint = getPaint(BACKPAINT);

        int r2 = (int) (Consts.lineTerminatorRadius*2);

        int gox = gammaInclusive ? 0 : (int) ((Math.cos(mInRadians())) * r2);
        int goy = gammaInclusive ? 0 : (int) ((Math.sin(mInRadians())) * r2);
        int tox = thetaInclusive ? 0 : (int) ((Math.cos(mInRadians())) * r2);
        int toy = thetaInclusive ? 0 : (int) ((Math.sin(mInRadians())) * r2);

        if(!lefterOrHigher(new PixelDot(gx,gy).gridDot())){
            gox = -gox;
            goy = -goy;
        } else {
            tox = -tox;
            toy = -toy;
        }

        if(getDrawingSettings().showStrike) canvas.drawLine(gx + gox, gy + goy, tx + tox, ty + toy, backPaint);
        canvas.drawLine(gx+gox, gy+goy, tx+tox, ty+toy, mainPaint);
    }

    @Override
    protected void drawSingleTerminator(Canvas canvas, Path polygon, GridDot dot, float r) {
        float x = dot.pixelX();
        float y = dot.pixelY();

        if(gammaInclusive && dot.equals(gamma.gridDot()) || thetaInclusive && dot.equals(theta.gridDot())) {
            canvas.drawCircle(x, y, r, getPaint(MAINPAINT));
        } else {
            if(!length().isZero()) {
                float r2 = r * 2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && HALFCIRCLEMODE) {
                    float deg = mInDegrees();

                    // Se il punto é "piú a sinistra" e, in caso di paritá, "piú in alto",
                    // inverti l'angolo
                    if(lefterOrHigher(dot)){
                        deg -= 180;
                    }

                    Path p = new Path();
                    p.addArc(x - r2, y - r2, x + r2, y + r2, deg + 90, 180);
                    canvas.drawPath(p, getPaint(EXCLUSIVEPAINT));
                } else {
                    canvas.drawCircle(x, y, r2, getPaint(EXCLUSIVEPAINT));
                }
            }
        }

    }

    private boolean lefterOrHigher(GridDot dot) {
        boolean imGamma = dot.equals(gamma.gridDot());

        if(m() == Consts.INFINITE){
            // Verticale
            if( imGamma && gamma.gridY() < theta.gridY() ||
                    !imGamma && gamma.gridY() > theta.gridY())
            {
                return true;
            }
        } else {
            // Cerco il "piú a sinistra"
            if( imGamma && gamma.gridX() < theta.gridX() ||
                    !imGamma && gamma.gridX() > theta.gridX())
            {
                return true;
            }
        }

        return false;
    }


}
