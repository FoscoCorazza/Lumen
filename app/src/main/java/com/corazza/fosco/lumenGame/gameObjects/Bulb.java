package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

public class Bulb extends SchemeLayoutDrawable {

    private static final String NEEDBOX = "BHNEEDBOX";
    private static final String MAIN = "BHMAIN";
    private static final String STROKE = "BHSTROKE";
    private static final String TEXT = "BHTXT";

    private static final int SATURATED = 1;
    private static final int UNSATURATED = 2;
    private static final int MAIN_COLOR = Palette.get().getBack(Palette.Gradiation.DARKKK);
    private static final int SECONDARY_COLOR = Palette.get().getAnti(Palette.Gradiation.LUMOUS);
    private static final int ORBITANT = 1000;
    private static final long ORBITANT_TIME = 2000;
    private static final long ORBITANT_OFFSET = 3;
    private static final long ORBITANT_TIME_OFFSET = 100;

    protected int baseSize = Consts.baseGridSize / 2;
    protected int needSize = baseSize / 3;
    protected float satMultp = 1.5f;

    protected int rotation1 = 0;
    protected int rotation2 = 0;
    protected float rotation1Speed = 0.5f;
    protected float rotation2Speed= 0.25f;
    private int need = 1;
    protected boolean needHidden  = false;
    private boolean saturationNotified = false;
    private boolean overSaturationNotified = false;
    private boolean underSaturationNotified = false;
    protected boolean forwardRotation = true;
    public boolean drawCore = true;

    private int coreAlpha = 100;
    private final int INACTIVE_ALPHA = 100;
    private final int ACTIVE_ALPHA = 255;
    private boolean winnerBulb = false;
    private boolean renewWhenAnimationFinishes = false;
    private boolean saturated = false;

    public Bulb(Dot dot) {
        super(dot);
    }

    public void initPaints() {
        Paints.put(NEEDPAINTID(), SECONDARY_COLOR);
        Paints.put(STROKE, SECONDARY_COLOR, 1, Paint.Style.STROKE);
        Paints.put(MAINPAINTID(), MAIN_COLOR);
        Paints.put(TEXT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(16), Consts.detailFont);
    }

    private String MAINPAINTID() {
        return MAIN + position.XMLString();
    }

    private String NEEDPAINTID() {
        return NEEDBOX + position.XMLString();
    }

    public void render(Canvas canvas) {
        int x = (int) position.pixelX(), y = (int) position.pixelY();

        //if(!needHidden && !orbitantForward()) drawOrbitants(canvas, x, y, size, Paints.get(NEEDPAINTID(), alpha()));

        if(drawCore) {
            Paint strokePaint = Paints.get(STROKE, alpha());
            Paint mainPaint = Paints.get(MAINPAINTID(), extAlpha(coreAlpha));

            canvas.drawCircle(x, y, size/3, mainPaint);
            canvas.drawCircle(x, y, size/3, strokePaint);
            canvas.drawCircle(x, y, size/3 + 8, strokePaint);
            drawFilamento(canvas, x, y, size/3, strokePaint);
            drawBase(canvas,x, y, size/3, strokePaint);
        }

        if(!needHidden && need > 0) {
            canvas.drawCircle(x+size/2+5, y+size/2+5, needSize, Paints.get(NEEDPAINTID(), alpha()/2));
            canvas.drawText(Integer.toString(need), x+(size/2)+4, y+(size/2)+ scaledFrom480Int(8), Paints.get(TEXT, alpha()));
        }

        //if(!needHidden && orbitantForward()) drawOrbitants(canvas, x, y, size, Paints.get(NEEDPAINTID(), alpha()));



    }

    private boolean orbitantForward() {
        long C = getTimeElapsed(ORBITANT) / ORBITANT_TIME;
        return C % 2 == 0;
    }

    private void drawOrbitants(Canvas canvas, int x, int y, int size, Paint paint) {
        double [] angles = {30, -30, 0};
        if(need > angles.length) angles = new double[]{30, -30, 60, -60, 0};
        if(need > angles.length) angles = new double[]{30, -30, 60, -60, 90, 90, 0, 20};

        for (int i = 0; i < need; i++) {
            drawOrbitant(canvas, i, x, y, size, angles[i], paint);
        }
    }



    private void drawOrbitant(Canvas canvas,int id,  int cx, int cy, int orbit, double angle, Paint paint) {
        long o = id * ORBITANT_TIME_OFFSET;
        orbit += id * ORBITANT_OFFSET;
        float ox = orbit * (float) Math.sin(angle);
        float oy = orbit * (float) Math.cos(angle);

        float x = valueOfNow(ORBITANT + id, cx + ox, cx - ox, 0, ORBITANT_TIME + o, AnimType.REPEAT);
        float y = valueOfNow(ORBITANT + id, cy + oy, cy - oy, 0, ORBITANT_TIME + o, AnimType.REPEAT);

        canvas.drawCircle(x, y ,5, paint);
    }

    private void drawBase(Canvas canvas, int x, int y, int size, Paint paint) {
        float i = size/2;
        float j = size/2;
        float o = (float) (size/3 + 3.5 * Math.log(size) + 1);
        float a = 2;
        float[] pts = { x-i,      y+i +o,
                        x-i,      y+i +o +j,
                        x-i,      y+i +o +j,
                        x-i+a,    y+i +o + a +j,
                        x-i+a,    y+i +o + a +j,
                        x+i-a,    y+i +o + a +j,
                        x+i-a,    y+i +o + a +j,
                        x+i,     y+i +o +j,
                        x+i,     y+i +o +j,
                        x+i,    y+i +o};

        canvas.drawLines(pts, paint);
    }

    public static void drawFilamento(Canvas canvas, int x, int y, int size, Paint paint) {
        float i = size/3;
        float j = size/4;
        float[] pts = { x,      y+size,
                        x,      y+size -2*j,
                        x,      y+size -2*j,
                        x-i,    y+size -3*j,
                        x-i,    y+size -3*j,
                        x+i,    y+size -4*j,
                        x+i,    y+size -4*j,
                        x-i,    y+size -5*j,
                        x-i,    y+size -5*j,
                        x+i,    y+size -6*j};

        canvas.drawLines(pts, paint);
    }


    public void renderOldBulb(Canvas canvas, float opacity) {
        int x = (int) position.pixelX(), y = (int) position.pixelY();

        float left   = x    - (5*size)/8;
        float top    = y    - (5*size)/8;
        float right  = left + (5*size)/4;
        float bottom = top  + (5*size)/4;
        int padding  = size/18;

        canvas.save();
        canvas.rotate(rotation2, (float)x, (float)y);
        canvas.drawRect(left, top, right, bottom, Paints.get(MAINPAINTID(), alpha()));
        canvas.restore();

        left   = x -    size/2;
        top    = y -    size/2;
        right  = left + size;
        bottom = top +  size;

        if(forwardRotation){
            canvas.save();
            canvas.rotate(rotation1, (float)x, (float)y);
        }
        canvas.drawRect(left, top, right, bottom, Paints.get(NEEDPAINTID(), alpha()));
        canvas.drawRect(left-padding, top-padding, right+padding, bottom+padding, Paints.get(STROKE, alpha()));

        if(forwardRotation)  canvas.restore();


        if(drawCore) {
            canvas.drawCircle(x, y, size/5, Paints.get(MAINPAINTID(), alpha()));
            canvas.drawCircle(x, y, size/5 + 3, Paints.get(STROKE, alpha()));
        }

        if(!needHidden) {
            canvas.drawCircle(x+(size/2)+5, y+(size/2+5), 15, Paints.get(NEEDPAINTID(), alpha()));
            canvas.drawText(Integer.toString(need), x+(size/2)+5, y+(size/2)+5+5, Paints.get(TEXT, alpha()));
        }




    }

    int baseColor = SECONDARY_COLOR;
    int saturationColor = Palette.get().getMain(Palette.Gradiation.NORMAL);
    int underSaturationColor = Palette.get().getHipo();
    int overSaturationColor = Palette.get().getNega();


    private int saturationTime = 500;


    public void update(){
        updateOpacity();
        int redvalue, greenvalue, bluevalue;

        int baseRed   = Color.red(baseColor);
        int baseBlue  = Color.blue(baseColor);
        int baseGreen = Color.green(baseColor);


        if(saturationNotified && getTimeElapsed(SATURATED) < saturationTime) {
            size = (int) valueOfNow(SATURATED, baseSize, baseSize*satMultp, 0, saturationTime, AnimType.BOUNCER);
            coreAlpha = (int) valueOfNow(SATURATED, INACTIVE_ALPHA, ACTIVE_ALPHA, 0, saturationTime, AnimType.DEFAULT);
            redvalue   = (int) valueOfNow(SATURATED, Color.red(MAIN_COLOR), Color.red(saturationColor), 0, saturationTime, AnimType.DEFAULT);
            greenvalue = (int) valueOfNow(SATURATED, Color.green(MAIN_COLOR), Color.green(saturationColor), 0, saturationTime, AnimType.DEFAULT);
            bluevalue  = (int) valueOfNow(SATURATED, Color.blue(MAIN_COLOR),  Color.blue(saturationColor), 0, saturationTime, AnimType.DEFAULT);
            Paints.get(MAINPAINTID(), alpha()).setARGB(255, redvalue, greenvalue, bluevalue);

            underSaturationNotified = false;
            overSaturationNotified = false;

        } else if(overSaturationNotified && getTimeElapsed(UNSATURATED) < saturationTime) {

            size = (int) (baseSize * halfSineValue(1.0f, 0.5f));
            coreAlpha = INACTIVE_ALPHA;
            redvalue   = (int) halfSineValue(baseRed, Color.red(overSaturationColor));
            greenvalue = (int) halfSineValue(baseGreen, Color.green(overSaturationColor));
            bluevalue  = (int) halfSineValue(baseBlue,  Color.blue(overSaturationColor));
            Paints.get(NEEDPAINTID(), alpha()).setARGB(255, redvalue, greenvalue, bluevalue);
            Paints.get(MAINPAINTID(), alpha()).setARGB(255, redvalue, greenvalue, bluevalue);

            saturationNotified = false;
            underSaturationNotified = false;

        }else if(underSaturationNotified && getTimeElapsed(UNSATURATED) < saturationTime) {

            size = (int) (baseSize * halfSineValue(1.0f, 0.5f));
            coreAlpha = INACTIVE_ALPHA;
            redvalue   = (int) halfSineValue(baseRed, Color.red(underSaturationColor));
            greenvalue = (int) halfSineValue(baseGreen, Color.green(underSaturationColor));
            bluevalue  = (int) halfSineValue(baseBlue,  Color.blue(underSaturationColor));
            Paints.get(NEEDPAINTID(), alpha()).setARGB(255, redvalue, greenvalue, bluevalue);
            Paints.get(MAINPAINTID(), alpha()).setARGB(255, redvalue, greenvalue, bluevalue);

            saturationNotified = false;
            overSaturationNotified = false;
        } else if (saturationNotified && getTimeElapsed(SATURATED) >= saturationTime) {
            // Significa che ho finito l'animazione di saturazione
            notifySaturationOutside();
            baseSize *= satMultp;
            size = baseSize;
            coreAlpha =  ACTIVE_ALPHA;
            saturationNotified = false;
            renewIfRequested();
        }else {
            size = baseSize;

            if(anyAnimationFinished()){
                renewIfRequested();
            }

            if(!saturated) Paints.get(MAINPAINTID(), alpha()).setColor(MAIN_COLOR);

            saturationNotified = false;
            underSaturationNotified = false;
            overSaturationNotified = false;
        }


        rotation1 = (int) valueOfNow(0, 359, 0, (long) (1000.0f / rotation1Speed), AnimType.INFINITE);
        rotation2 = (int) valueOfNow(359, 0, 0, (long) (1000.0f / rotation2Speed), AnimType.INFINITE);
    }

    private boolean anyAnimationFinished() {
        boolean over = overSaturationNotified && getTimeElapsed(UNSATURATED) >= saturationTime;
        boolean undr = underSaturationNotified && getTimeElapsed(UNSATURATED) >= saturationTime;
        boolean rigt = saturationNotified && getTimeElapsed(SATURATED) >= saturationTime;
        return over || undr || rigt;
    }

    private void renewIfRequested() {
        if(renewWhenAnimationFinishes) renew();
    }

    private SchemeLayout notificationSender;
    private void notifySaturationOutside() {
        if(winnerBulb) notificationSender.saturationNotified();
    }

    public boolean isNeedHidden() {
        return needHidden;
    }

    public void setNeedHidden(boolean needHidden) {
        this.needHidden = needHidden;
    }

    public boolean isSaturated() {
        return saturated;
    }

    public void notifySaturation(SchemeLayout sender, boolean win) {
        winnerBulb = win;
        notificationSender = sender;
        saturationNotified = true;
        saturated = true;
        setTimeElapsed(SATURATED, 0);
    }

    public void notifyOverSaturation() {
        overSaturationNotified = true;
        setTimeElapsed(UNSATURATED, 0);
    }

    public void notifyUnderSaturation() {
        underSaturationNotified = true;
        setTimeElapsed(UNSATURATED, 0);
    }

    public void notifyReset() {
        Paints.get(NEEDPAINTID(), alpha()).setColor(SECONDARY_COLOR);
    }

    // Utility, per rendere il codice un po' più leggibile:
    private float halfSineValue(float bgnValue, float endValue) {
        return halfSineValue(UNSATURATED, bgnValue, endValue);
    }

    private float halfSineValue(int id, float bgnValue, float endValue) {
        return valueOfNow(id, bgnValue, endValue, 0, saturationTime, AnimType.HALFSINE);

    }

    public int getNeed() {
        return need;
    }

    public void setNeed(int need) {
        this.need = need;
        for (int i = 0; i < need; i++) {
            setTimeElapsed(ORBITANT+i, 0);
        }
    }

    @Override
    public void renew() {
        opacity = 1;
        isFadingIn = false;
        isFadingOut =false;
        baseSize = Consts.baseGridSize / 2;
        size = baseSize;
        saturationNotified = false;
        underSaturationNotified = false;
        overSaturationNotified = false;
        coreAlpha = INACTIVE_ALPHA;
        Paints.get(MAINPAINTID(), alpha()).setColor(MAIN_COLOR);
    }

    public void renewWhenAnimationFinishes() {
        saturated = false;
        if(!saturationNotified && !underSaturationNotified && !overSaturationNotified){
            renew();
            renewWhenAnimationFinishes = false;
        } else {
            renewWhenAnimationFinishes = true;
        }
    }
}
