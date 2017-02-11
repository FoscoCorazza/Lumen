package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.savemanager.SchemeResult;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone on 22/08/2016.
 */
public class Level extends SchemeLayoutDrawable {


    private static final String MAIN = "LVLMAIN";
    private static final String TEXT = "LVLTEXT";
    private static final String STROKE_UNLOCKED = "LVLSTROKE1";
    private static final String STROKE_LOCKED = "LVLSTROKE2";

    private static final String STAR_OBTAINED = "LVLSTAR1";
    private static final String STAR_LOCKED = "LVLSTAR2";

    private String id;
    private Integer act_points;
    private boolean requirements;
    private boolean pressed = false;
    private LevelClickListener listener;
    private boolean unlocked;
    private static float TEXTPH;

    public Level(String id, boolean requirements) {
        this.id = id;
        this.requirements = requirements;
    }

    @Override
    protected void initPaints() {
        Paints.put(MAIN, Palette.get().getBack(Palette.Gradiation.DARKKK));
        Paints.put(STROKE_UNLOCKED, Palette.get().getMain(Palette.Gradiation.NORMAL), scaledInt(5), Paint.Style.STROKE);
        Paints.put(STROKE_LOCKED, Palette.get().getBack(Palette.Gradiation.LUMOUS), scaledInt(5), Paint.Style.STROKE);

        Paints.put(STAR_OBTAINED, Palette.get().getMain(Palette.Gradiation.NORMAL));
        Paints.put(STAR_LOCKED, Palette.get().getBack(Palette.Gradiation.LUMOUS), scaledInt(3), Paint.Style.STROKE);
        Paints.put(TEXT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledInt(45), Consts.detailFont, Paint.Align.CENTER);
        TEXTPH = Paints.get(TEXT, alpha()).descent() - Paints.get(TEXT, alpha()).ascent();
    }

    @Override
    public void render(Canvas canvas) {
        if (opacity <= 0) return;

        // Variables
        Paint STRKE = Paints.get(isUnlocked() ? STROKE_UNLOCKED : STROKE_LOCKED, alpha());
        Paint MAINP = Paints.get(MAIN, alpha());
        Paint TEXTP = Paints.get(TEXT, alpha());
        int px = (int) (pixelX() + offset.pixelX());
        int py = (int) (pixelY() + offset.pixelY());

        // Body
        int s2 = HalfWidth();
        canvas.drawCircle(px, py, s2, MAINP);
        canvas.drawCircle(px, py, s2, STRKE);

        //Stars
        int ssize = scaledInt(12);
        int s = getTotPoints();
        int deg = 36;
        int dist =  s2 - ssize*2;
        for(int i = 0; i < s; i++) {
            boolean active = i < getActualPoints();
            Paint STARPAINT = Paints.get(active ? STAR_OBTAINED : STAR_LOCKED, alpha());

            canvas.save();
            canvas.rotate((i * deg) - (s-1)*(deg/2), px, py);
            canvas.drawCircle(px, py - dist, ssize, STARPAINT);

            canvas.restore();
        }


        Utils.drawCenteredTextWithTextH(canvas, Utils.trimCode(id), px,py, TEXTP, TEXTPH);


    }

    private int getTotPoints() {
        //TODO: Ci piace cosí?
        return 5;
    }

    @Override
    public void render(Canvas canvas, int x1, int y1) {
        offset.change(x1,y1);
        render(canvas);
    }

    private int HalfWidth() {
        return (int) (size / 2.3f);
    }


    private int getActualPoints() {
        if(act_points == null) {
            SchemeInfo info = Consts.schemeList.get(id);
            act_points = 0;
            if(info != null && info.getResult() != null){
                act_points = info.getResult().getTotal();
            }
        }
        return act_points;
    }


    public void setPosition(Dot position) {
        this.position = position;
    }

    public Dot getPosition() {
        return position;
    }


    public boolean isSavedAsUnlocked() {
        act_points = null;
        if(!requirements) return true;
        SchemeInfo info = Consts.schemeList.get(id);
        if(info != null){
            SchemeResult result = info.getResult();
            if(result != null){
                return result.isSbloccato();
            }
        }
        return false;
    }

    public boolean checkLevelRequirement() {
        act_points = null;

        int numericCode = Integer.parseInt(id);
        if(numericCode > 0) {
            SchemeInfo info = Consts.schemeList.get(Utils.prevCode(id));
            if (info != null) {
                if (info.getResult() != null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event, Dot offset) {
        if(!unlocked) return false;
        Dot touchedAt = new PixelDot(event.getRawX() + offset.pixelX(), event.getRawY() + offset.pixelY());
        boolean onButton = onButton(touchedAt);
        int action = event.getAction();
        if(action == MotionEvent.ACTION_MOVE && !onButton) {
            pressed = false;
        }else if(opacity>0 && onButton){
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    pressed = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (pressed) {
                        pressed = false;
                        if(listener != null) listener.onTap(id);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    private boolean onButton(Dot dot) {
        int s = (int) HalfWidth();
        int minx = (int) (position.pixelX() - s);
        int miny = (int) (position.pixelY() - s);
        int maxx = (int) (position.pixelX() + s);
        int maxy = (int) (position.pixelY() + s);

        return dot.pixelX() > minx && dot.pixelX() < maxx &&
                dot.pixelY() > miny && dot.pixelY() < maxy;

    }

    public void setListener(LevelClickListener listener) {
        this.listener = listener;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isUnlocked() {
        return unlocked;
    }


    public interface LevelClickListener{
        void onTap(String id);
    }
}
