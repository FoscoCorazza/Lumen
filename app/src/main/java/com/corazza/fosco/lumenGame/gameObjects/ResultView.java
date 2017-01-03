package com.corazza.fosco.lumenGame.gameObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.gameObjects.huds.Button;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone on 14/08/2016.
 */
public class ResultView extends SchemeLayoutDrawable {

    private static final String STROKE = "ENDSCREENSTROKE";
    private static final String TEXT = "ENDSCREENBHTXT";
    private static final Dot MIN_SIZE = new GridDot(6, 6);
    private static final Dot MIN_CENTER = new GridDot(4, 7);
    private static final Dot MAX_SIZE = new GridDot(7, 10);
    protected int total = 0;
    protected int picked = 0;
    protected Context context;
    private Grid grid;

    public ResultView(Context context) {
        initPaints();
        this.context = context;
        this.opacity = 0;
    }

    @Override
    protected void initPaints() {
        Paints.put(STROKE, Consts.Colors.MATERIAL_LIGHTEST_GREY, 1, Paint.Style.STROKE);
        Paints.put(TEXT, Consts.Colors.WHITE, scaledInt(23), Consts.detailFont, Paint.Align.CENTER);
    }

    @Override
    public void render(Canvas canvas) {
        Dot size    = MIN_SIZE;
        Dot center  = MIN_CENTER;

        if(grid != null){
            size   = Dot.min(Dot.max(grid.getMaxSize().add(2), MIN_SIZE), MAX_SIZE);
            center = grid.getCenter();
        }

        drawStroke(canvas, center, size, new GridDot(1,4), Paints.get(STROKE, alpha()));
        drawString(canvas, center, size, Consts.getString(context, Consts.EndLevelString1), Consts.getString(context, Consts.EndLevelString2), Paints.get(TEXT, alpha()));
    }

    private void drawString(Canvas canvas, Dot center, Dot size, String string1, String string2, Paint paint) {
        string2 = string2.replace("%picked%", String.valueOf(picked));
        string2 = string2.replace("%total%", String.valueOf(total));
        float h = (paint.descent() - paint.ascent()) / 2 - 2;

        canvas.drawText(string1, center.pixelX(), center.pixelY() - size.pixelY()/2 + h, paint);
        canvas.drawText(string2, center.pixelX(), center.pixelY() + size.pixelY()/2 + h, paint);
    }

    private void drawStroke(Canvas canvas, Dot center, Dot size, Dot angleAndTextspace, Paint paint){
        Dot halfSize = size.half();
        float a  = angleAndTextspace.pixelX();
        float ts = angleAndTextspace.pixelY();

        // Calcolo gli angoli del rettangolo circoscritto al mio rettangolo ad angoli smussati.
        float NEx = center.pixelX() + halfSize.pixelX(), NEy = center.pixelY() - halfSize.pixelY();
        float NOx = center.pixelX() - halfSize.pixelX(), NOy = center.pixelY() - halfSize.pixelY();
        float SEx = center.pixelX() + halfSize.pixelX(), SEy = center.pixelY() + halfSize.pixelY();
        float SOx = center.pixelX() - halfSize.pixelX(), SOy = center.pixelY() + halfSize.pixelY();


        // Linee:
        // Sono sei, quattro orizzontali e due verticali.
        float l = ( size.pixelX() - 2*a - ts) / 2;

        drawLineH(canvas, NOx + a, NOy, + l, paint);
        drawLineH(canvas, NEx - a, NEy, - l, paint);
        drawLineH(canvas, SOx + a, SOy, +l, paint);
        drawLineH(canvas, SEx - a, SEy, - l, paint);

        drawLineV(canvas, NOx, NOy + a, size.pixelY() - 2 * a, paint);
        drawLineV(canvas, NEx, NEy + a, size.pixelY() - 2 * a, paint);

        // Angoli:
        drawArc(canvas, NOx, NOy, + a, + a, 180, paint);
        drawArc(canvas, NEx, NEy, - a, + a, -90,  paint);
        drawArc(canvas, SEx, SEy, - a, - a, 0, paint);
        drawArc(canvas, SOx, SOy, + a, - a, 90,   paint);

    }


    private void drawArc(Canvas canvas, float x, float y, float ox, float oy, float startAngle, Paint paint){
        float l = Math.min(x, x+2*ox);
        float r = Math.max(x, x + 2 * ox);
        float t = Math.min(y, y + 2 * oy);
        float b = Math.max(y, y + 2 * oy);

        canvas.drawArc(new RectF(l,t,r,b), startAngle, 90, false, paint);
    }

    private void drawLineH(Canvas canvas, float x, float y, float l, Paint paint){
        canvas.drawLine(x,y,x+l,y,paint);
    }

    private void drawLineV(Canvas canvas, float x, float y, float l, Paint paint){
        canvas.drawLine(x,y,x,y+l,paint);

    }

    public void setStarsValue(int picked, int total) {
        this.picked = picked;
        this.total = total;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }


}
