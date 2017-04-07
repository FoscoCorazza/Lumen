package com.corazza.fosco.lumenGame.gameObjects.huds;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

public class Hud extends SchemeLayoutDrawable {

    private static final String TEXT = "HUDTXT";

    public static final int START = 0;
    public static final int STOP = 1;
    public static final int RESET = 2;
    public static final int ERASE = 3;
    public static final int MODE = 4;
    public static final int BACK = 5;

    Button[] buttons;
    SchemeLayout caller;

    public Hud(SchemeLayout schemeLayout) {
        super(new PixelDot(0,0));
        caller = schemeLayout;
        buttons    = new Button[6];
        int h0 = scaledFrom480Int(80);
        int l1 = scaledFrom480Int(100);
        int l2 = scaledFrom480Int(180);



        buttons[START] = new Button(
                R.drawable.scheme_hud_button_play,
                new PixelDot(Consts.W/2, h0), schemeLayout, Button.Action.START, true);

        buttons[STOP] = new Button(
                R.drawable.scheme_hud_button_stop,
                new PixelDot(Consts.W/2, h0), schemeLayout, Button.Action.STOP, true);

        buttons[RESET] = new Button(
                R.drawable.scheme_hud_button_reset,
                new PixelDot(Consts.W/2 - l1, h0), schemeLayout, Button.Action.RESET, true);

        buttons[ERASE] = new Button(
                R.drawable.scheme_hud_button_erase,
                new PixelDot(Consts.W/2 + l1, h0), schemeLayout, Button.Action.ERASE, true);

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
        Paints.put(TEXT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(18), Consts.detailFont, Paint.Align.CENTER);
    }

    public void render(Canvas canvas) {
        if(caller.isShowLength()){
            caller.getMinLength().drawOnCanvas(canvas, Consts.W / 2, scaledFrom480Int(18), Paints.get(TEXT, alpha()));
        }
        for (Button button : buttons) button.render(canvas);
    }

    public void update(){
        super.update();
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

    public void updateVisibility(Boolean secondaryButtons) {
        buttons[BACK].setInvisible(!secondaryButtons);
        buttons[MODE].setInvisible(!secondaryButtons);
    }

    public Button[] getButtons() {
        return buttons;
    }

    public Button getButton(int index) {
        return index < buttons.length ? buttons[index] : null;
    }

    public void setButtonActivated(int index, boolean activated) {
        if(getButton(index) != null) getButton(index).setActivated(activated);
    }
}