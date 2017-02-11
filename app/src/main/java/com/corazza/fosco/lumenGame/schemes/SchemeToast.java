package com.corazza.fosco.lumenGame.schemes;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

/**
 * Created by Simone on 08/08/2016.
 */
public class SchemeToast extends SchemeLayoutDrawable{

    private static final String BACKPAINT = "TOASTBACKPAINT";
    private static final String TEXTPAINT = "TOASTTEXTPAINT";

    String text = "";
    Dot completeSize;

    public SchemeToast(String s) {
        init(s, new GridDot(0, Consts.hStep+2), new GridDot(Consts.hStep, 2));
    }

    private void init(String text, Dot position, Dot size) {
        initPaints();
        this.position = position;
        this.text = text;
        completeSize = size;
    }


    @Override
    protected void initPaints() {
        Paints.put(BACKPAINT, Palette.get().getBack(Palette.Gradiation.DARKKK));
        Paints.put(TEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(16), Consts.detailFont, Paint.Align.CENTER);
    }

    @Override
    public void render(Canvas canvas) {
        int x1 = (int) position.pixelX();
        int y1 = (int) position.pixelY();
        int x2 = (int) (x1 + completeSize.pixelX());
        int y2 = (int) (y1 + completeSize.pixelY());

        Paint tPaint = Paints.get(TEXTPAINT, alpha());

        canvas.drawRect(x1,y1,x2,y2, Paints.get(BACKPAINT, extAlpha(150)));

        Utils.drawCenteredText(canvas, text, (x1+x2)/2,(y1+y2)/2, tPaint);

    }
}
