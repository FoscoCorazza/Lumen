package com.corazza.fosco.lumenGame.schemes.schemeLayout;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.activities.MainActivity;
import com.corazza.fosco.lumenGame.activities.MenuActivity;
import com.corazza.fosco.lumenGame.activities.SchemeCreatorActivity;
import com.corazza.fosco.lumenGame.gameObjects.Bulb;
import com.corazza.fosco.lumenGame.gameObjects.SegmentCreatorListener;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;

import static com.corazza.fosco.lumenGame.helpers.Consts.*;
import static com.corazza.fosco.lumenGame.helpers.Consts.TITLE;
import static com.corazza.fosco.lumenGame.helpers.Consts.baseGridSize;

public class TitleSchemeLayout extends SchemeLayout {

    private static final String LINEPAINT = "SIMPLELINEPAINT";
    private static final String TEXTPAINT = "TITLETEXTPAINT";
    private static final String BIGTEXTPAINT = "TITLEBIGTEXTPAINT";

    public TitleSchemeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTouchEnabled(false);
        setDrawElements(false);
        Paints.put(LINEPAINT,    Consts.Colors.WHITE, Consts.lineW, Paint.Style.FILL);
        Paints.put(TEXTPAINT,    Consts.Colors.WHITE, (int) (0.0375f* W), detailFont, Paint.Align.RIGHT);
        Paints.put(BIGTEXTPAINT, Consts.Colors.WHITE, (int) (0.2460f* W), detailFont, Paint.Align.CENTER);
    }

    private void text_drawOn(Canvas canvas){
        final int alpha = (int) valueOfNow(0, 255, 500, 3000);
        final Paint mainPaint = Paints.get(BIGTEXTPAINT, alpha);
        final Paint secoPaint = Paints.get(TEXTPAINT, alpha);

        int y = (int) (gridCalc( 5) - (mainPaint.ascent() + mainPaint.descent()));
        int l = (int) mainPaint.measureText(TITLE);

        int xPreTitle = l + (W-l)/2;
        int yPreTitle = y - W/5;

        canvas.drawText(PRETITLE, xPreTitle, yPreTitle, secoPaint);
        canvas.drawText(TITLE, W/2, y, mainPaint);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            canvas.drawText("Versione: " + version, W - 10, H - 10, secoPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Secret Button
        int bx = (W - l) / 2 - 1;
        int by = y+15;
        int bw = (int) mainPaint.measureText(TITLE.substring(0,1)) + 20;
        int bh = (int) (mainPaint.ascent() + mainPaint.descent()) - 40;


        secretButtonA = new Rect(bx,by,bx+bw, by+bh);
        bx+=4*bw-10;
        secretButtonB = new Rect(bx,by,bx+bw, by+bh);


    }

    private Rect secretButtonA;
    private Rect secretButtonB;

    @Override
    public void render(Canvas canvas){
        if(canvas != null) {
            clear(canvas);
            bckg.render(canvas);
            grid.render(canvas);
            text_drawOn(canvas);
            blbs.render(canvas);
            line_drawOn(canvas);
        }
    }

    private void line_drawOn(Canvas canvas) {
        long timeForOne = 150;
        float myX1, myY1, myX2, myY2;

        // Linea in alto orizzontale
        long bgTime = 0;
        long enTime = 7 * timeForOne + bgTime;
        float bgSize = 0;
        float enSize = gridCalc(-2, W);
        float bgPosi = gridCalc(-1, H);
        float enPosi = gridCalc(1);

        myX1 = valueOfNow(bgPosi, enPosi, bgTime, enTime);
        myY1 = gridCalc(1);
        myX2 = myX1 + valueOfNow(bgSize, enSize, bgTime, enTime);
        myY2 = gridCalc(1);

        canvas.drawLine(myX1, myY1, myX2, myY2, Paints.get(LINEPAINT, 255));

        // Linea in alto verticale
        bgTime = enTime;
        enTime = 3 * timeForOne + bgTime;
        bgSize = 0;
        enSize = gridCalc(3);

        myX1 = gridCalc(1);
        myY1 = gridCalc(1);
        myX2 = gridCalc(1);
        myY2 = myY1 + valueOfNow(bgSize, enSize, bgTime, enTime);

        canvas.drawLine(myX1, myY1, myX2, myY2, Paints.get(LINEPAINT, 255));

        // Linea in basso verticale
        bgTime = 3 * timeForOne + enTime;
        enTime = 5 * timeForOne + bgTime;
        bgSize = 0;
        enSize = gridCalc(-8, H);

        myX1 = gridCalc(1);
        myY1 = gridCalc(7);
        myX2 = gridCalc(1);
        myY2 = myY1 + valueOfNow(bgSize, enSize, bgTime, enTime);

        canvas.drawLine(myX1, myY1, myX2, myY2, Paints.get(LINEPAINT, 255));


        // Linea verticale indicatore inizio
        bgTime = enTime - 2 * timeForOne;
        enTime = timeForOne + bgTime;
        bgSize = 0;
        enSize = gridCalc(1);

        myX1 = gridCalc(0, W / 2);
        myY1 = gridCalc(11);
        myX2 = gridCalc(0, W / 2);
        myY2 = myY1 + valueOfNow(bgSize, enSize, bgTime, enTime);

        canvas.drawLine(myX1, myY1, myX2, myY2, Paints.get(LINEPAINT, 255));

        // Linea orizzontale indicatore inizio
        bgTime = enTime;
        enTime = timeForOne + bgTime;
        bgSize = 0;
        enSize = gridCalc(1);

        myX1 = gridCalc(0, W / 2);
        myY1 = gridCalc(12);
        myX2 = myX1 + valueOfNow(bgSize, enSize, bgTime, enTime);
        myY2 = gridCalc(12);

        canvas.drawLine(myX1, myY1, myX2, myY2, Paints.get(LINEPAINT, 255));

        canvas.drawText(getContext().getText(R.string.begin).toString(),
                6 * baseGridSize, 12 * baseGridSize + 5,
                Paints.get(TEXTPAINT, (int) valueOfNow(0, 255, bgTime, enTime)));
    }

    private float gridCalc(double grid) {
        return gridCalc(grid, 0);
    }

    private float gridCalc(double grid, int offset) {
        int r = (int) (offset + baseGridSize*grid);
        return r - (r % baseGridSize);
    }

    @Override
    public void onStartButtonClick(){}

    private boolean tapped = false;
    private int secretTappedA = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!tapped) {
            if(isTouchingSecretButtonA(event)){
                secretTappedA++;
                if(secretTappedA < 3)
                    firstBulb().notifyOverSaturation();
                else {
                    tapped = true;
                    SchemeCreatorActivity.play(getActivity(), "4G01");
                }
            } else if(isTouchingSecretButtonB(event)) {
                firstBulb().notifyOverSaturation();
                SaveFileManager.clear(getActivity());
            } else {
                tapped = true;
                firstBulb().notifySaturation(this, true);
            }
        }
        return super.onTouchEvent(event);
    }

    @NonNull
    private Bulb firstBulb() {
        return blbs.get(0);
    }

    private boolean isTouchingSecretButtonB(MotionEvent event) {
        return isTouchingSecretButtonB(new PixelDot(event.getRawX(), event.getRawY()));
    }

    private boolean isTouchingSecretButtonB(Dot dot) {
        int miny = Math.min(secretButtonB.top, secretButtonB.bottom);
        int minx = Math.min(secretButtonB.left, secretButtonB.right);
        int maxx = Math.max(secretButtonB.right, secretButtonB.left);
        int maxy = Math.max(secretButtonB.bottom, secretButtonB.top);

        return dot.pixelX() > minx && dot.pixelX() < maxx &&
                dot.pixelY() > miny && dot.pixelY() < maxy;

    }

    private boolean isTouchingSecretButtonA(MotionEvent event) {
        return isTouchingSecretButtonA(new PixelDot(event.getRawX(), event.getRawY()));
    }

    private boolean isTouchingSecretButtonA(Dot dot) {
        int miny = Math.min(secretButtonA.top, secretButtonA.bottom);
        int minx = Math.min(secretButtonA.left, secretButtonA.right);
        int maxx = Math.max(secretButtonA.right, secretButtonA.left);
        int maxy = Math.max(secretButtonA.bottom, secretButtonA.top);

        return dot.pixelX() > minx && dot.pixelX() < maxx &&
                dot.pixelY() > miny && dot.pixelY() < maxy;

    }



    @Override
    protected OnTouchListener getSegmentCreator() {
        return new SegmentCreatorListener(this, false);
    }


    public void saturationNotified() {
        startActivity(MenuActivity.class);
    }

}
