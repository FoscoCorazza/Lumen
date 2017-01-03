package com.corazza.fosco.lumenGame.gameObjects.huds;

import android.graphics.Canvas;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone Chelo on 10/10/2016.
 */

public class ResultHud extends Hud {

    public ResultHud(SchemeLayout schemeLayout) {
        super(schemeLayout);
        buttons    = new Button[3];

        int h = scaledInt(80);
        int l = scaledInt(100);

        buttons[0] = new Button(
                R.drawable.scheme_hud_button_next,
                new PixelDot(Consts.W/2, h), schemeLayout, Button.Action.NEXT, true);

        buttons[1] = new Button(
                R.drawable.scheme_hud_button_undo,
                new PixelDot(Consts.W/2 + l, h), schemeLayout, Button.Action.REDO, true);

        buttons[2] = new Button(
                R.drawable.scheme_hud_button_menu,
                new PixelDot(Consts.W/2 - l, h), schemeLayout, Button.Action.MENU, true);

        buttons[0].setOrbitant(true);
    }

    @Override
    public void render(Canvas canvas) {
        for (Button button : buttons) button.render(canvas);
    }

    public void setButtonsEnabled(boolean b) {}

}
