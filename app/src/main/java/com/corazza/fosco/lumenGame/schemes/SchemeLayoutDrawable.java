package com.corazza.fosco.lumenGame.schemes;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.corazza.fosco.lumenGame.animations.AlphaAnimation;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Utils;

public abstract class SchemeLayoutDrawable {
    private static final int MAIN = 0;

    public float opacity = 1;
    public Dot position;
    protected PixelDot offset = new PixelDot(0,0);
    public int size = Consts.baseGridSize;

    private AlphaAnimation fadeInAnimation  = new AlphaAnimation(this, 0, 255, 1000);
    private AlphaAnimation fadeOutAnimation = new AlphaAnimation(this, 255, 0, 1000);

    // Costruttore
    public SchemeLayoutDrawable(Dot position){
        this.position = position;
        initPaints();
    }

    public SchemeLayoutDrawable() {
        initPaints();
    }

    // Funzioni di disegno
    protected abstract void initPaints();
    public abstract void render(Canvas canvas);
    public void update() {updateAnimations();}
    public void startAnimation() { setTimeElapsed(0); }
    public float pixelX() {return position.pixelX();}
    public float pixelY() {return position.pixelY();}

    // Gestione del tempo
    private SparseArray<Long> bgnTime = new SparseArray<>();
    public long getTimeElapsed(int action){
        if (bgnTime.get(action) != null) {
            return System.currentTimeMillis() - bgnTime.get(action);
        } else {
            return 0;
        }
    }
    protected long getTimeElapsed(){
        return getTimeElapsed(0);
    }

    public void setTimeElapsed(int action, long ms){
        bgnTime.put(action, System.currentTimeMillis() - ms);
    }
    protected void setTimeElapsed(long ms){
        setTimeElapsed(MAIN, ms);
    }

    protected float valueOfNow(float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) getTimeElapsed(), bgnPoint, endPoint, bgnTime, endTime, type);
    }

    public float valueOfNow(int action, float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) getTimeElapsed(action), bgnPoint, endPoint, bgnTime, endTime, type);
    }

    protected float valueAtTimeX(long x, float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        return Utils.valueOfNow((float) x, bgnPoint, endPoint, bgnTime, endTime, type);
    }

    public void startFadeOut() {
        fadeOutAnimation.start();
    }

    public void startFadeIn() {
        fadeInAnimation.start();
    }

    private void updateAnimations(){
        fadeInAnimation.update();
        fadeOutAnimation.update();

        if(fadeInAnimation.isActive())  opacity = fadeInAnimation.getOpacity();
        if(fadeOutAnimation.isActive()) opacity = fadeOutAnimation.getOpacity();

    }

    public boolean hasFadedIn(){
        return fadeInAnimation.isActive() && !fadeInAnimation.isOngoing();
    }

    public boolean hasFadedOut(){
        return fadeOutAnimation.isActive() && !fadeOutAnimation.isOngoing();
    }

    public int alpha() {
        return (int) (opacity*255);
    }

    protected int extAlpha(int extAlpha) {
        return (int) (opacity*extAlpha);
    }

    public void inherit(SchemeLayoutDrawable drawable) {
        fadeOutAnimation = drawable.fadeOutAnimation;
        fadeInAnimation = drawable.fadeInAnimation;
        opacity = drawable.opacity;
    }

    public void renew() {
        opacity = 1;
        fadeOutAnimation.revert();
        fadeInAnimation.revert();
    }

    public boolean isIn(Dot dot) {
        return  (position != null && position.equals(dot));
    }

    public void render(Canvas canvas, int x1, int y1) {
        render(canvas);
    }
}
