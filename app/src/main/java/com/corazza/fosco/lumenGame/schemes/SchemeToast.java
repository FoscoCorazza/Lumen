package com.corazza.fosco.lumenGame.schemes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.Log;
import android.util.Pair;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaled;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone on 08/08/2016.
 */
public class SchemeToast extends SchemeLayoutDrawable{

    private static final String BACKPAINT = "TOASTBACKPAINT";
    private static final String TEXTPAINT = "TOASTTEXTPAINT";

    String text = "";
    Dot completeSize;

    public SchemeToast(String s) {
        init(s, new GridDot(0, 10), new GridDot(Consts.hStep, 2));
    }

    private void init(String text, Dot position, Dot size) {
        initPaints();
        this.position = position;
        this.text = text;
        completeSize = size;
    }


    @Override
    protected void initPaints() {
        Paints.put(BACKPAINT, Consts.Colors.MATERIAL_BLACK);
        Paints.put(TEXTPAINT, Consts.Colors.WHITE, scaledInt(16), Consts.detailFont, Paint.Align.CENTER);
    }

    @Override
    public void render(Canvas canvas) {
        int x1 = (int) position.pixelX();
        int y1 = (int) position.pixelY();
        int x2 = (int) (x1 + completeSize.pixelX());
        int y2 = (int) (y1 + completeSize.pixelY());

        Paint tPaint = Paints.get(TEXTPAINT, alpha());
        float h = tPaint.descent() - tPaint.ascent();

        canvas.drawRect(x1,y1,x2,y2, Paints.get(BACKPAINT, extAlpha(150)));

        String[] splits = text.split("\n");
        int textX = (x1+x2)/2;
        int textY = (int) ((y1+y2+h/2)/2);

        textY -= h*(splits.length-1)/2;

        for (String line : splits) {
            canvas.drawText(line, textX, textY, tPaint);
            textY += h;
        }

    }
}
