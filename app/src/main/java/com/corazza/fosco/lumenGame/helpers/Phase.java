package com.corazza.fosco.lumenGame.helpers;

/**
 * Created by Simone on 15/08/2016.
 */
public enum Phase {
    USER_PLAYING, LUMEN_PLAYING, COMPLETING_TRANSITION, RESULT;

    public boolean isPhaseOfPlaying(){
        return this == USER_PLAYING || this == LUMEN_PLAYING;
    }

    public boolean isPhaseOfPlayinOrTransition(){
        return this == USER_PLAYING || this == LUMEN_PLAYING || this == COMPLETING_TRANSITION;
    }

    public boolean isPhaseOfResult(){
        return this == RESULT || this == COMPLETING_TRANSITION;
    }
}
