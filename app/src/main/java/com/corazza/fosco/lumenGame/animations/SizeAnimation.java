package com.corazza.fosco.lumenGame.animations;

import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

/**
 * Created by Simone on 11/02/2017.
 */

public class SizeAnimation extends Animation {

    private float minSize;
    private float maxSize;
    private int duration;
    private float sizeOfNow;

    public SizeAnimation(SchemeLayoutDrawable drawable, float minSize, float maxSize, int duration) {
        super(drawable);
        init(minSize, maxSize, duration);
    }

    private void init(float minSize, float maxSize, int duration){
        this.sizeOfNow = minSize;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.duration = duration;
    }

    @Override
    public void update() {
        if (isOngoing() && isActive()) {
            sizeOfNow = drawable.valueOfNow(id, minSize, maxSize, 0, duration, AnimType.HALFSINE);
            if (drawable.getTimeElapsed(id) > duration) {
                setOngoing(false);
                sizeOfNow = minSize;
            }
        } else {
            sizeOfNow = minSize;
        }
    }

    public float getSizeMultiplier() {
        return sizeOfNow;
    }

}
