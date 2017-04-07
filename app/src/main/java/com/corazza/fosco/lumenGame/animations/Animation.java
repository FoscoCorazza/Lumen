package com.corazza.fosco.lumenGame.animations;


import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.util.Random;

/**
 * Created by Simone on 11/02/2017.
 */

public abstract class Animation {

    protected int id;
    protected SchemeLayoutDrawable drawable;
    private boolean active = false;
    private boolean ongoing = false;

    public Animation(SchemeLayoutDrawable drawable){
        this.id = (new Random().nextInt(Integer.MAX_VALUE));
        this.drawable = drawable;
    }

    public void start() {
        setOngoing(true);
        setActive(true);
        drawable.setTimeElapsed(id, 0);
    }

    public abstract void update();

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void revert(){
        this.active = false;
    }


}
