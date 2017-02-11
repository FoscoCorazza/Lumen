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
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.SoundsHelper;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;

import static com.corazza.fosco.lumenGame.helpers.Consts.*;
import static com.corazza.fosco.lumenGame.helpers.Consts.TITLE;
import static com.corazza.fosco.lumenGame.helpers.Consts.baseGridSize;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

public class TitleSchemeLayout extends SchemeLayout {

    private static final String LINEPAINT = "SIMPLELINEPAINT";
    private static final String TEXTPAINT = "TITLETEXTPAINT";
    private static final String BIGTEXTPAINT = "TITLEBIGTEXTPAINT";

    public TitleSchemeLayout(Context context) {
        super(context);
    }

    protected void init() {
        super.init();
        setTouchEnabled(false);
        setDrawElements(false);
        Paints.put(LINEPAINT,    Palette.get().getAnti(Palette.Gradiation.LUMOUS), Consts.lineW, Paint.Style.FILL);
        Paints.put(TEXTPAINT,    Palette.get().getAnti(Palette.Gradiation.LUMOUS), (int) (0.0375f* W), detailFont, Paint.Align.RIGHT);
        Paints.put(BIGTEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), (int) (0.2200f* W), detailFont, Paint.Align.CENTER);
    }

    private void text_drawOn(Canvas canvas){
        final int alpha = (int) valueOfNow(0, 255, 500, 3000);
        final Paint mainPaint = Paints.get(BIGTEXTPAINT, alpha);
        final Paint secoPaint = Paints.get(TEXTPAINT, alpha);

        int l = (int) mainPaint.measureText(TITLE);

        int xPreTitle = l + (W-l)/2 - scaledInt(11);
        int yPreTitle = (int) (gridCalc(5) - scaledInt(118));

        canvas.drawText(PRETITLE, xPreTitle, yPreTitle, secoPaint);
        Utils.drawCenteredText(canvas, TITLE, W/2, (int) gridCalc(5) + scaledInt(15), mainPaint);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            canvas.drawText("Version: " + version, W - scaledInt(20), H - scaledInt(20) , secoPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Secret Buttons
        int bx = (W - l) / 2 - 1;
        int by = (int) gridCalc(4);
        int bw = (int) mainPaint.measureText(TITLE.substring(0,1)) + scaledInt(20);
        int bh = (int) gridCalc(2);


        secretButtonL = new Rect(bx,by,bx+bw, by+bh);
        bx+=(int) mainPaint.measureText(TITLE.substring(0,2)) +20;
        secretButtonM = new Rect(bx,by,bx+bw, by+bh);
        bx+=(int) mainPaint.measureText(TITLE.substring(2,4)) +20;
        secretButtonN = new Rect(bx,by,bx+bw, by+bh);

        // canvas.drawRect(secretButtonL, mainPaint);
        // canvas.drawRect(secretButtonM, mainPaint);
        // canvas.drawRect(secretButtonN, mainPaint);

    }

    private Rect secretButtonL;
    private Rect secretButtonM;
    private Rect secretButtonN;

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
        float bgPosi = gridCalc(-1, W);
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
        enSize = gridCalc(-7, H);

        myX1 = gridCalc(1);
        myY1 = gridCalc(6);
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
            if(isTouchingSecretButtonL(event)){
                secretTappedA++;
                if(secretTappedA < 3)
                    firstBulb().notifyOverSaturation();
                else {
                    tapped = true;
                    SchemeCreatorActivity.play(getActivity(), "4G01");
                }
            } else if(isTouchingSecretButtonM(event)) {
                SoundsHelper.getInstance().swap();
            } else if(isTouchingSecretButtonN(event)) {
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

    private boolean isTouchingSecretButtonN(MotionEvent event) {
        return isTouchingSecretButtonN(new PixelDot(event.getRawX(), event.getRawY()));
    }

    private boolean isTouchingSecretButtonN(Dot dot) {
        int miny = Math.min(secretButtonN.top, secretButtonN.bottom);
        int minx = Math.min(secretButtonN.left, secretButtonN.right);
        int maxx = Math.max(secretButtonN.right, secretButtonN.left);
        int maxy = Math.max(secretButtonN.bottom, secretButtonN.top);

        return dot.pixelX() > minx && dot.pixelX() < maxx &&
                dot.pixelY() > miny && dot.pixelY() < maxy;

    }


    private boolean isTouchingSecretButtonM(MotionEvent event) {
        return isTouchingSecretButtonM(new PixelDot(event.getRawX(), event.getRawY()));
    }

    private boolean isTouchingSecretButtonM(Dot dot) {
        int miny = Math.min(secretButtonM.top, secretButtonM.bottom);
        int minx = Math.min(secretButtonM.left, secretButtonM.right);
        int maxx = Math.max(secretButtonM.right, secretButtonM.left);
        int maxy = Math.max(secretButtonM.bottom, secretButtonM.top);

        return dot.pixelX() > minx && dot.pixelX() < maxx &&
                dot.pixelY() > miny && dot.pixelY() < maxy;

    }

    private boolean isTouchingSecretButtonL(MotionEvent event) {
        return isTouchingSecretButtonL(new PixelDot(event.getRawX(), event.getRawY()));
    }

    private boolean isTouchingSecretButtonL(Dot dot) {
        int miny = Math.min(secretButtonL.top, secretButtonL.bottom);
        int minx = Math.min(secretButtonL.left, secretButtonL.right);
        int maxx = Math.max(secretButtonL.right, secretButtonL.left);
        int maxy = Math.max(secretButtonL.bottom, secretButtonL.top);

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
