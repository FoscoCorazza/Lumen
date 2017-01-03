package com.corazza.fosco.lumenGame.schemes.schemeLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.activities.MainActivity;
import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.gameObjects.Level;
import com.corazza.fosco.lumenGame.gameObjects.SegmentCreatorListener;
import com.corazza.fosco.lumenGame.gameObjects.huds.Button;
import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.schemes.DList;

import java.util.ArrayList;
import java.util.List;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

public class MenuSchemeLayout extends SchemeLayout implements Level.LevelClickListener {

    private static final String TEXTPAINT = "TITLETEXTPAINT";
    private DList<Level> lvls;
    private Button next, prev;
    private boolean sectorCompleted = false;

    public MenuSchemeLayout(Context context) {
        super(context);
        init();
    }

    protected void init() {
        Paints.put(TEXTPAINT, Consts.Colors.WHITE, scaledInt(23), Consts.detailFont, Paint.Align.CENTER);
        setTouchEnabled(false);
        setDrawElements(false);
    }

    @Override
    public void render(Canvas canvas){
        if(canvas != null) {
            clear(canvas);
            bckg.render(canvas);
            grid.render(canvas);
            path.render(canvas);
            lvls.render(canvas);
            bttn_render(canvas);
            titl_render(canvas);
        }
    }

    private void bttn_render(Canvas canvas) {
        if(next != null && sectorCompleted) next.render(canvas);
        if(prev != null && !FirstSector()) prev.render(canvas);
    }

    private void titl_render(Canvas canvas) {
        canvas.drawText(getName(), Consts.W/2, new GridDot(0,1).pixelY(), Paints.get(TEXTPAINT, 255));
    }


    @Override
    public void onStartButtonClick(){
        replaceLayoutWith(+1);
    }

    @Override
    public void onBackButtonClick(){
        replaceLayoutWith(-1);
    }

    private void replaceLayoutWith(int o) {

        try {
            String newCode = String.format("%03d", Integer.parseInt(getCode()) + o);
            MenuSchemeLayout msl = Consts.getMenuSchemeLayout(newCode, new MenuSchemeLayout(getContext()));
            if (msl != null) {
                setCode(msl.getCode());
                setName(msl.getName());
                setGrid(msl.getGrid());
                setPath(msl.getPath());
                setLevels(msl.getLevels());
                setButtons();
            }
        } catch (Exception ignored) {}



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(next!= null && next.onTouch(event)) return true;
        if(prev!= null && prev.onTouch(event)) return true;
        for(Level l : getLevels()){
            boolean r = l.onTouchEvent(event);
            if(r) return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected OnTouchListener getSegmentCreator() {
        return new SegmentCreatorListener(this, false);
    }

    public void setLevels(List<Level> levels) {
        setLevelsVisibility(levels);

        lvls = new DList<>();
        lvls.addAll(levels);
    }

    private void setLevelsVisibility(List<Level> levels) {
        sectorCompleted = true;
        for(Level l : levels){
            l.setListener(this);
            if(l.isSavedAsUnlocked()){
                l.setVisible(true);
            } else {
                boolean value = l.checkLevelRequirement(levels);
                l.setVisible(value);
                // TODO: Se sbloccato, qui è un buon punto per scriverlo.
            }
            sectorCompleted &= (l.isCompleted());
        }

    }

    public List<Level> getLevels() {
        return lvls;
    }

    @Override
    public void onClick(String id) {
        MainActivity.play(getActivity(), id);
    }

    public void setButtons() {

        int y = Consts.H/2 + scaledInt(18);
        int x1 = FirstSector() ? Consts.W/2 : Consts.W/2 + scaledInt(60);
        int x2 = Consts.W/2 - scaledInt(60);

        next = new Button(R.drawable.scheme_hud_button_next_s,
                new PixelDot(x1, y), this, Button.Action.START, true);

        prev = new Button(R.drawable.scheme_hud_button_back,
                new PixelDot(x2, y), this, Button.Action.BACK, true);

        prev.setEnabled(!FirstSector());
        prev.setInvisibleIfDisabled(true);

        next.setEnabled(sectorCompleted);
        next.setInvisibleIfDisabled(true);



    }

    private boolean FirstSector() {
        return "000".equals(code);
    }


}
