package com.corazza.fosco.lumenGame.gameObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.lists.Dlist;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone on 14/08/2016.
 */
public class ResultView extends SchemeLayoutDrawable {
    protected Context context;

    private static final String STROKE = "ENDSCREENSTROKE";
    private static final String TEXT = "ENDSCREENBHTXT";
    private static final String TEXTD = "ENDSCREENBHTXTD";
    private static final Dot MIN_SIZE = new GridDot(5, 5);
    private static final Dot MIN_CENTER = new GridDot(3, 5);
    private int total = 0;
    private int picked = 0;
    private int waste = 0;

    private Dlist<Star> obulbs = new Dlist<>();
    private Dlist<Star> wasted = new Dlist<>();
    private Dlist<Star> perfct = new Dlist<>();
    private Dlist<Star> result = new Dlist<>();
    private boolean animationFinished;

    public ResultView(Context context) {
        initPaints();
        this.context = context;
        this.opacity = 0;
        this.animationFinished = false;
        setStars();

    }

    private void setStars() {
        Dot size = MIN_SIZE;
        Dot center = MIN_CENTER;

        int dist = 80;
        int apotemaX = (int) (size.pixelX()/2);
        int apotemaY = (int) (size.pixelY()/2);
        int f = (int) (size.pixelY() / 10);
        int cx = (int) center.pixelX(), cy = (int) center.pixelY();
        int rghtLimit = cx + apotemaX - scaledInt(80) - (new Star(GridDot.Zero).getRadius());

        for(int i = 0; i< 3; i++){
            obulbs.add(new Star(new PixelDot(rghtLimit - i * dist, cy - apotemaY + 3*f)));
        }

        wasted.add(new Star(new PixelDot(rghtLimit, cy - apotemaY + 5*f)));
        perfct.add(new Star(new PixelDot(rghtLimit, cy - apotemaY + 7*f)));

        for(int i = 0; i < 5; i++){
            result.add(new Star(new PixelDot(cx - (i-2) * dist, cy - apotemaY + 9*f)));
        }

        obulbs.inherit(this);
        wasted.inherit(this);
        perfct.inherit(this);
        result.inherit(this);

    }

    @Override
    protected void initPaints() {
        Paints.put(STROKE, Palette.get().getAnti(Palette.Gradiation.BRIGHT), 1, Paint.Style.STROKE);
        Paints.put(TEXT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(23), Consts.detailFont, Paint.Align.CENTER);
        Paints.put(TEXTD, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(20), Consts.detailFont, Paint.Align.LEFT);

    }

    @Override
    public void update() {
        super.update();

        obulbs.update();
        wasted.update();
        perfct.update();
        result.update();

    }

    @Override
    public void render(Canvas canvas) {
        Dot size    = MIN_SIZE;
        Dot center  = MIN_CENTER;

        drawStroke(canvas, center, size, new GridDot(0.5, 3), Paints.get(STROKE, alpha()));
        drawString(canvas, center, size);
    }

    private void drawString(Canvas canvas, Dot center, Dot size) {
        String title = context.getString(R.string.EndLevelString1);
        String achv1 = context.getString(R.string.EndLevelAchievment1);
        String achv2 = context.getString(R.string.EndLevelAchievment2);
        String achv3 = context.getString(R.string.EndLevelAchievment3);

        int apotemaX = (int) (size.pixelX()/2);
        int apotemaY = (int) (size.pixelY()/2);
        int f = (int) (size.pixelY() / 10);
        int cx = (int) center.pixelX(), cy = (int) center.pixelY();
        int leftLimit = cx - apotemaX + scaledInt(80);

        Utils.drawCenteredText(canvas, title, cx, cy - apotemaY, Paints.get(TEXT, alpha()));
        Utils.drawCenteredText(canvas, achv1, leftLimit, cy - apotemaY + 3*f, Paints.get(TEXTD, alpha()));
        Utils.drawCenteredText(canvas, achv2, leftLimit, cy - apotemaY + 5*f, Paints.get(TEXTD, alpha()));
        Utils.drawCenteredText(canvas, achv3, leftLimit, cy - apotemaY + 7*f, Paints.get(TEXTD, alpha()));

        obulbs.render(canvas);
        perfct.render(canvas);
        wasted.render(canvas);
        result.render(canvas);

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
        // Sono sei, tre orizzontali e due verticali.
        float l = ( size.pixelX() - 2*a - ts) / 2;

        drawLineH(canvas, NOx + a, NOy, + l, paint);
        drawLineH(canvas, NEx - a, NEy, - l, paint);
        drawLineH(canvas, SOx + a, SOy, size.pixelX() - 2 * a, paint);
        //drawLineH(canvas, SOx + a, SOy, +l, paint);
        //drawLineH(canvas, SEx - a, SEy, - l, paint);

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

    public void setWastedLums(int waste) {
        this.waste = waste;
    }


    @Override
    public void startFadeIn() {
        super.startFadeIn();
        (new StarPickerRunnable()).execute();
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    private class StarPickerRunnable extends AsyncTask<Object, Object, Object> {

        private static final int WAIT = 200;
        private static final int DELAY = 1000;

        private void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int r = 0;
            sleep(DELAY);
            for(int i = 0; i < picked; i++){
                obulbs.get(i).setPicked(true);
                sleep(WAIT);
                r++;
            }
            for(int i = picked; i < total; i++){
                obulbs.get(i).setPicked(false);
            }

            if(waste == 0) {
                wasted.get(0).setPicked(true);
                sleep(WAIT);
                r++;
            } else {
                wasted.get(0).setPicked(false);
            }

            if(waste == 0 && picked == total) {
                perfct.get(0).setPicked(true);
                sleep(WAIT);
                r++;
            } else {
                perfct.get(0).setPicked(false);
            }


            sleep(WAIT*2);

            for(int i = 0; i < r; i++){
                result.get(4-i).setPicked(true);
            }

            animationFinished = true;
            return null;
        }
    }


}
