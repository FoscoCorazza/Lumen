package com.corazza.fosco.lumenGame.gameObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;
import com.corazza.fosco.lumenGame.savemanager.SchemeResult;
import com.corazza.fosco.lumenGame.schemes.DList;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

/**
 * Created by Simone on 22/08/2016.
 */
public class Level extends SchemeLayoutDrawable {


    private static final String MAIN_INACTIVE = "LVLMAIN";
    private static final String MAIN_ACTIVE = "LVLMAINACTIVE";
    private static final String STROKE = "LVLSTROKE";

    private String id;
    private int stars;
    private Integer act_star;
    private boolean requirements;
    private Boolean completed = null;
    private ArrayList<String> unlockedLevels = new ArrayList<>();
    private DList<Path> unlockedPaths = new DList<>();
    private boolean pressed = false;
    private LevelClickListener listener;

    public Level(String id, int stars, boolean requirements) {
        this.id = id;
        this.stars = stars;
        this.requirements = requirements;
    }

    @Override
    protected void initPaints() {
        Paints.put(STROKE, Consts.Colors.MATERIAL_WHITE, 1, Paint.Style.STROKE);
        Paints.put(MAIN_INACTIVE, Consts.Colors.MATERIAL_BLACK);
        Paints.put(MAIN_ACTIVE, Consts.Colors.MATERIAL_GREEN);
    }

    @Override
    public void render(Canvas canvas) {
        if(isCompleted()){
            unlockedPaths.render(canvas);
        }

        float px = pixelX(), py = pixelY();

        if(opacity > 0) {
            float s2 = HalfWidth();
            canvas.drawRect(px - s2, py - s2, px + s2, py + s2, Paints.get((getActualStars()>=stars) ? MAIN_ACTIVE : MAIN_INACTIVE, alpha()));
            canvas.drawRect(px - s2, py - s2, px + s2, py + s2, Paints.get(STROKE, alpha()));
        }

        //Stars
        {
            int s = stars;
            int r = (s+scaledInt(6)), rr = (int) Math.sqrt(r*r+r*r);
            int xO = s%2 == 0 ? -r : 0;
            int yO = s<6      ? (s%2 == 0 ? -r : -rr) : 0;

            //Controllo su singola stella:
            xO = s == 1 ? 0 : xO;
            yO = s == 1 ? 0 : yO;

            for(int i = 0; i < s; i++) {
                boolean active = i < getActualStars();
                canvas.save();
                canvas.rotate(i * (360.0f / s), px, py);
                canvas.drawCircle(px - xO, py - yO, scaledInt(7), Paints.get(active ? MAIN_ACTIVE : MAIN_INACTIVE, alpha()));
                canvas.drawCircle(px - xO, py - yO, scaledInt(7), Paints.get(STROKE, alpha()));
                canvas.restore();
            }
        }

    }

    private float HalfWidth() {
        return size / 2.3f;
    }

    public boolean isCompleted() {
        if(completed == null) {
            SchemeInfo info = Consts.schemeList.get(id);
            completed = info != null && info.getResult() != null;
        }
        return completed;
    }

    private int getActualStars() {
        if(act_star == null) {
            SchemeInfo info = Consts.schemeList.get(id);
            act_star = 0;
            if(info != null && info.getResult() != null){
                act_star = info.getResult().getStars();
            }
        }
        return act_star;
    }

    private void recalcCompleted(){
        completed = null;
        act_star = null;
        isCompleted();
    }

    public void setPosition(Dot position) {
        this.position = position;
    }

    public Dot getPosition() {
        return position;
    }

    public void addUnlockedLevel(String nextId) {
        unlockedLevels.add(nextId);
    }
    public void addUnlockedLevels(String[] nextId) {
        unlockedLevels.addAll(new ArrayList<>(Arrays.asList(nextId)));
    }

    public void addUnlockedPath(Path path) {
        unlockedPaths.add(path);
    }

    public void setVisible(boolean value){
        opacity = value ? 1 : 0;
    }

    public boolean isSavedAsUnlocked() {
        recalcCompleted();
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

    public boolean checkLevelRequirement(List<Level> levels) {
        recalcCompleted();
        for (Level level : levels) {
            if(level.unlockedLevels != null && level.unlockedLevels.contains(id)){
                SchemeInfo info = Consts.schemeList.get(level.id);
                if(info != null){
                    if(info.getResult() != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean onButton = onButton(new PixelDot(event.getRawX(), event.getRawY()));
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
                        if(listener != null) listener.onClick(id);
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

    public int getStars() {
        return stars;
    }


    public interface LevelClickListener{
        void onClick(String id);
    }
}
