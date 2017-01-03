package com.corazza.fosco.lumenGame.gameObjects.huds;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

public class Hud extends SchemeLayoutDrawable {

    protected static final String TEXT = "HUDTXT";

    private static final int START = 0;
    private static final int STOP = 1;
    private static final int RESET = 2;
    private static final int UNDO = 3;
    private static final int MODE = 4;
    private static final int BACK = 5;

    protected Button[] buttons;
    protected SchemeLayout caller;

    public Hud(SchemeLayout schemeLayout) {
        super(new PixelDot(0,0));
        caller = schemeLayout;
        buttons    = new Button[6];
        int h0 = scaledInt(80);
        int l1 = scaledInt(100);
        int l2 = scaledInt(180);



        buttons[START] = new Button(
                R.drawable.scheme_hud_button_play,
                new PixelDot(Consts.W/2, h0), schemeLayout, Button.Action.START, true);

        buttons[STOP] = new Button(
                R.drawable.scheme_hud_button_stop,
                new PixelDot(Consts.W/2, h0), schemeLayout, Button.Action.STOP, true);

        buttons[RESET] = new Button(
                R.drawable.scheme_hud_button_reset,
                new PixelDot(Consts.W/2 - l1, h0), schemeLayout, Button.Action.RESET, true);

        buttons[UNDO] = new Button(
                R.drawable.scheme_hud_button_undo,
                new PixelDot(Consts.W/2 + l1, h0), schemeLayout, Button.Action.UNDO, true);

        buttons[MODE] = new Button(
                R.drawable.scheme_hud_button_hide,
                new PixelDot(Consts.W/2 + l2, h0), schemeLayout, Button.Action.MODE, true);

        buttons[BACK] = new Button(
                R.drawable.scheme_hud_button_menu,
                new PixelDot(Consts.W/2 - l2, h0), schemeLayout, Button.Action.BACK, true);


        buttons[START].setOrbitant(true);
        buttons[START].setInvisibleIfDisabled(true);
        buttons[STOP].setInvisibleIfDisabled(true);
        buttons[STOP].setEnabled(false);

    }

    @Override
    protected void initPaints() {
        Paints.put(TEXT, Consts.Colors.WHITE, scaledInt(18), Consts.detailFont, Paint.Align.CENTER);
    }

    public void render(Canvas canvas) {
        if(caller.isShowLength()){
            caller.getMinLength().drawOnCanvas(canvas, Consts.W / 2, scaledInt(18), Paints.get(TEXT, alpha()));
        }
        for (Button button : buttons) button.render(canvas);
    }

    public void update(){
        updateOpacity();
        for(SchemeLayoutDrawable drawable : buttons){
            drawable.inherit(this);
            drawable.update();
        }
    }

    public boolean onTouch(MotionEvent event) {

        if(opacity == 0) return false;

        for (Button button : buttons)
            if (button.onTouch(event)) return true;

        return false;
    }

    public void setButtonsEnabled(boolean b) {
        for(Button button : buttons){
            button.setEnabled(b);
        }
        buttons[STOP].setEnabled(!b);
    }

    public void updateVisibility(SchemeLayout schemeLayout) {
        if(!schemeLayout.hasSecondaryButtons()){
            buttons[BACK].setInvisible(true);
            buttons[MODE].setInvisible(true);
        }
    }
}