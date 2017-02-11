package com.corazza.fosco.lumenGame.schemes;
import android.graphics.Canvas;

import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Utils;

import java.util.HashMap;

public abstract class SchemeLayoutDrawable {
    private static final int MAIN = 0;
    private static final int FADING = -1;
    protected static final int FADING_TIME = 1000; // in Millisecondi

    public float  opacity = 1;
    public Dot position;
    protected PixelDot offset = new PixelDot(0,0);
    public int size = Consts.baseGridSize;
    protected boolean isFadingOut = false;
    protected boolean isFadingIn  = false;

    // Costruttore
    public SchemeLayoutDrawable(Dot position){
        this.position = position;
        initPaints();
    }

    public SchemeLayoutDrawable() {initPaints();}

    // Funzioni di disegno
    protected abstract void initPaints();
    public abstract void render(Canvas canvas);
    public void update() {updateOpacity();}
    public void startAnimation() { setTimeElapsed(0); }
    public float pixelX() {return position.pixelX();}
    public float pixelY() {return position.pixelY();}

    // Gestione del tempo
    private HashMap<Integer, Long> bgnTime = new HashMap<>();
    protected long getTimeElapsed(int action){
        if (bgnTime.get(action) != null) {
            return System.currentTimeMillis() - bgnTime.get(action);
        } else {
            return 0;
        }
    }
    protected long getTimeElapsed(){
        return getTimeElapsed(0);
    }

    protected void setTimeElapsed(int action, long ms){
        bgnTime.put(action, System.currentTimeMillis() - ms);
    }
    protected void setTimeElapsed(long ms){
        setTimeElapsed(MAIN, ms);
    }

    protected float valueOfNow(float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) getTimeElapsed(), bgnPoint, endPoint, bgnTime, endTime, type);
    }

    protected float valueOfNow(int action, float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) getTimeElapsed(action), bgnPoint, endPoint, bgnTime, endTime, type);
    }

    protected float valueAtTimeX(long x, float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) x, bgnPoint, endPoint, bgnTime, endTime, type);
    }

    public void notifyFadeOut() {
        setTimeElapsed(FADING, 0);
        isFadingOut = true;
    }

    public void notifyFadeIn() {
        setTimeElapsed(FADING, 0);
        isFadingIn = true;
    }

    protected void updateOpacity(){
        if(isFadingOut) {
            opacity = valueOfNow(FADING, 1, 0, 0, FADING_TIME, AnimType.DEFAULT);
        } else if(isFadingIn){
            opacity = valueOfNow(FADING, 0,1,0, FADING_TIME, AnimType.DEFAULT);
        }

    }

    public boolean hasFadedIn(){
        return isFadingIn && getTimeElapsed(FADING) > FADING_TIME;
    }

    public boolean hasFadedOut(){
        return isFadingOut && getTimeElapsed(FADING) > FADING_TIME;
    }

    public int alpha() {
        return (int) (opacity*255);
    }

    protected int extAlpha(float extAlpha) {
        return (int) (opacity*extAlpha);
    }

    public void inherit(SchemeLayoutDrawable drawable) {
        isFadingOut = drawable.isFadingOut;
        isFadingIn  = drawable.isFadingIn;
        opacity = drawable.opacity;
        setTimeElapsed(FADING, drawable.getTimeElapsed(FADING));
    }

    public void renew() {
        opacity = 1;
        isFadingIn = false;
        isFadingOut =false;
    }

    public boolean isIn(Dot dot) {
        return  (position != null && position.equals(dot));
    }

    public void render(Canvas canvas, int x1, int y1) {
        render(canvas);
    }
}
