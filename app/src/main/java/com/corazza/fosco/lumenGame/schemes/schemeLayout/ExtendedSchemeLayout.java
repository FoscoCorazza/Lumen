package com.corazza.fosco.lumenGame.schemes.schemeLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.gameObjects.huds.Button;
import com.corazza.fosco.lumenGame.gameObjects.huds.Hud;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Utils;

import java.util.Arrays;


/**
 * Created by Simone Chelo on 17/01/2017.
 */

public class ExtendedSchemeLayout extends SchemeLayout {

    Paint textL2R, textR2L;

    public ExtendedSchemeLayout(Context activity) {
        super(activity);
    }

    // Implementation:
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(Canvas canvas){
        super.render(canvas);
        if(is("004")) renderForLevel004(canvas);

    }

    @Override
    public void setCode(String code){
        super.setCode(code);
        if(hud1 != null) hud1.updateVisibility(!is("000", "001", "002", "003"));
    }

    // Extensions:

    private void initPaintsForLevel004() {
        if(textL2R == null || textR2L == null){
            textL2R = Paints.clone(Paints.TextPaint);
            textR2L = Paints.clone(Paints.TextPaintR2L);
        }
    }

    private void renderForLevel004(Canvas canvas) {
        int x1,x2,y1,y2;
        int d = 75, l = 50, d2 = d/2;
        int alpha = hud1.alpha();

        initPaintsForLevel004();
        textL2R.setAlpha(alpha);
        textR2L.setAlpha(alpha);

        // Menu
        Button menu = hud1.getButton(Hud.BACK);
        x1 = x2 = (int) menu.position.pixelX();
        y1 = (int) menu.position.pixelY() + d;
        y2 = y1 + l;
        canvas.drawLine(x1,y1,x2,y2, textL2R);
        canvas.drawText("Go back to the menu", x2 + d2, y2, textL2R);

        // Reset
        Button reset = hud1.getButton(Hud.RESET);
        x1 = x2 = (int) reset.position.pixelX();
        y1 = (int) reset.position.pixelY() - d;
        y2 = y1 - l;
        canvas.drawLine(x1,y1,x2,y2, textR2L);
        canvas.drawText("Reset the screen", x2 - d2, y2 + Utils.scaledInt(20), textR2L);

        // Undo
        Button undo = hud1.getButton(Hud.ERASE);
        x1 = x2 = (int) undo.position.pixelX();
        y1 = (int) undo.position.pixelY() + d;
        y2 = y1 + l;
        canvas.drawLine(x1,y1,x2,y2, textL2R);
        canvas.drawText("Erase the line you touch", x2 + d2, y2, textL2R);

        // Show
        Button show = hud1.getButton(Hud.MODE);
        x1 = x2 = (int) show.position.pixelX();
        y1 = (int) show.position.pixelY() - d;
        y2 = y1 - l;
        canvas.drawLine(x1,y1,x2,y2, textR2L);
        canvas.drawText("Show lines length", x2 - d2, y2 + Utils.scaledInt(20), textR2L);

    }

    // Utils:

    private boolean is(String... levels) {
        return Arrays.asList(levels).contains(code);
    }


}
