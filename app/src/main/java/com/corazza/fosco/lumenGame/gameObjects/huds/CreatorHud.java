package com.corazza.fosco.lumenGame.gameObjects.huds;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeCreatorLayout;

import java.util.Arrays;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

/**
 * Created by Simone Chelo on 10/10/2016.
 */
public class CreatorHud extends Hud {

    private static final int NHUDBT = 5;
    static final int BULB = NHUDBT+1;
    static final int LUMN = NHUDBT+2;
    static final int NEED = NHUDBT+3;
    static final int STAR = NHUDBT+4;
    static final int OBST = NHUDBT+5;
    static final int GRDH = NHUDBT+6;
    static final int GRDW = NHUDBT+7;
    static final int GRDD = NHUDBT+8;
    static final int HIDE = NHUDBT+9;
    static final int INCL = NHUDBT+10;


    public CreatorHud(SchemeCreatorLayout schemeCreatorLayout) {
        super(schemeCreatorLayout);

        buttons    = Arrays.copyOf(buttons, INCL+1);

        int xO = Consts.W/2;
        int xD = scaledFrom480Int(80);
        int yO = Consts.H - scaledFrom480Int(80);
        int yD = scaledFrom480Int(80);

        buttons[HIDE] = new Button(
                R.drawable.scheme_hud_button_hide,
                new PixelDot(xO, yO), caller, Button.Action.HIDE, true);

        buttons[LUMN] = new Button(
                R.drawable.scheme_hud_button_lumen,
                new PixelDot(xO - xD, yO), caller, Button.Action.LUMEN, true);

        buttons[STAR] = new Button(
                R.drawable.scheme_hud_button_star,
                new PixelDot(xO + xD, yO), caller, Button.Action.STAR, true);

        buttons[BULB] = new Button(
                R.drawable.scheme_hud_button_bulb,
                new PixelDot(xO - 2*xD, yO), caller, Button.Action.BULB, true);

        buttons[OBST] = new Button(
                R.drawable.scheme_hud_button_obst,
                new PixelDot(xO + 2*xD, yO), caller, Button.Action.OBST, true);


        buttons[GRDD] = new Button(
                R.drawable.scheme_hud_button_dots,
                new PixelDot(xO, yO-yD), caller, Button.Action.DOTS, true);

        buttons[GRDH] = new Button(
                R.drawable.scheme_hud_button_grid_height,
                new PixelDot(xO - xD, yO-yD), caller, Button.Action.GRID_H, true);

        buttons[GRDW] = new Button(
                R.drawable.scheme_hud_button_grid_width,
                new PixelDot(xO + xD, yO-yD), caller, Button.Action.GRID_W, true);

        buttons[NEED] = new Button(
                R.drawable.scheme_hud_button_need,
                new PixelDot(xO - 2*xD, yO-yD), caller, Button.Action.NEED, true);

        buttons[INCL] = new Button(
                R.drawable.scheme_hud_button_include,
                new PixelDot(xO + 2*xD, yO-yD), caller, Button.Action.INCLUDE, true);

    }

    public void activateOnly(Button.Action action){
        for(int i = NHUDBT +1; i <= INCL;i++){
            buttons[i].setActivated(buttons[i].getAction() == action);
        }
    }

    public void hideAll(Button.Action action, boolean hide) {
        for(int i = NHUDBT +1; i <= INCL;i++){
            buttons[i].setInvisible(buttons[i].getAction() != action && hide);
        }
    }
}
