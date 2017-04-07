package com.corazza.fosco.lumenGame.animations;

import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.util.Random;

/**
 * Created by Simone on 11/02/2017.
 */

public class AlphaAnimation extends Animation {

    private int minAlpha;
    private int maxAlpha;
    private int duration;
    private int alphaOfNow;

    public AlphaAnimation(SchemeLayoutDrawable drawable, int minAlpha, int maxAlpha, int duration) {
        super(drawable);
        init(minAlpha, maxAlpha, duration);
    }

    private void init(int minAlpha, int maxAlpha, int duration){
        this.alphaOfNow = minAlpha;
        this.minAlpha = minAlpha;
        this.maxAlpha = maxAlpha;
        this.duration = duration;
    }

    @Override
    public void update() {
        if (isOngoing() && isActive()) {
            alphaOfNow = (int) drawable.valueOfNow(id, minAlpha, maxAlpha, 0, duration, AnimType.DEFAULT);
            if (drawable.getTimeElapsed(id) > duration) {
                setOngoing(false);
                alphaOfNow = maxAlpha;
            }
        } else if (isActive()) {
            alphaOfNow = maxAlpha;
        } else {
            alphaOfNow = minAlpha;
        }
    }

    public int getAlpha() {
        return alphaOfNow;
    }

    public float getOpacity() {
        return alphaOfNow/255f;
    }
}
