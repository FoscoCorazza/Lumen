package com.corazza.fosco.lumenGame.schemes.schemeLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.activities.MainActivity;
import com.corazza.fosco.lumenGame.gameObjects.Level;
import com.corazza.fosco.lumenGame.gameObjects.SegmentCreatorListener;
import com.corazza.fosco.lumenGame.gameObjects.huds.Button;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.schemes.DList;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

public class MenuSchemeLayout extends SchemeLayout implements Level.LevelClickListener {


    final int LVL_PER_LINE = 5;

    private static final String TEXTPAINT = "TITLETEXTPAINT";
    private DList<Level> lvls;
    private int scrollValue = 0;
    private int height;

    private DissipatorRunnable dissipator = new DissipatorRunnable();

    public MenuSchemeLayout(Context context) {
        super(context);
    }

    protected void init() {
        super.init();

        setHeight(new PixelDot(Consts.W, Consts.H));

        Paints.put(TEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), scaledFrom480Int(23), Consts.detailFont, Paint.Align.CENTER);
        setTouchEnabled(false);
        setDrawElements(false);
    }

    private void setHeight(Dot dot) {
        height = ((int) dot.pixelY()) - Consts.H;
    }

    @Override
    public void render(Canvas canvas){
        if(canvas != null) {
            clear(canvas);
            bckg.render(canvas);
            grid.render(canvas);
            path.render(canvas, 0, -scrollValue);
            lvls.render(canvas, 0, -scrollValue);
            //titl_render(canvas);
        }
    }

    private void titl_render(Canvas canvas) {
        canvas.drawText(String.valueOf(scrollValue), Consts.W/2, new GridDot(0,1).pixelY(), Paints.get(TEXTPAINT, 255));
    }


    private VelocityTracker mVelocityTracker = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        for(Level l : getLevels()){
            boolean r = l.onTouchEvent(event, new PixelDot(0, scrollValue));
            if(r) return true;
        }

        return onScrollEvent(event) || super.onTouchEvent(event);
    }


    int beginRawY = 0;
    int beginScrollValue = 0;
    Integer firstPointerId = null;
    private boolean onScrollEvent(MotionEvent event) {

        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        int rawY = (int) event.getRawY();
        boolean rightPointer = firstPointerId != null && firstPointerId == pointerId;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initVelocityTracker(event);
                if(firstPointerId == null) {
                    beginRawY = rawY;
                    beginScrollValue = scrollValue;
                    firstPointerId = pointerId;
                    return true;
                }
            case MotionEvent.ACTION_MOVE:

                if(rightPointer) {
                    updateVelocity(event);
                    scrollValue = Utils.bounds(beginScrollValue + (-rawY + beginRawY), 0, height);
                    return true;
                }
            case MotionEvent.ACTION_UP:

                if(rightPointer) {
                    dissipator.setVelocity(updateVelocity(event).pixelY());
                    dissipator.run();
                    firstPointerId = null;
                    return true;
                }
        }

        return false;
    }

    private void initVelocityTracker(MotionEvent event) {
        if(mVelocityTracker == null) {
            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
            mVelocityTracker = VelocityTracker.obtain();
        }
        else {
            // Reset the velocity tracker back to its initial state.
            mVelocityTracker.clear();
        }
        // Add a user's movement to the tracker.
        mVelocityTracker.addMovement(event);
    }

    private PixelDot updateVelocity(MotionEvent event) {
        int pointerId = event.getPointerId(event.getActionIndex());
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(30);
        return new PixelDot(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId),
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId));
    }

    @Override
    protected OnTouchListener getSegmentCreator() {
        return new SegmentCreatorListener(this, false);
    }

    public void setLevels() {
        path = new Path();

        // Ultimo livello della prima riga.
        final int L = LVL_PER_LINE-1;
        int lastScrollValue = 0;

        // Conto quanti livelli esistono.
        lvls = new DList<>();
        TreeMap<String, SchemeInfo> schemes = Consts.schemeList;
        for(Map.Entry<String, SchemeInfo> entry : schemes.entrySet()){
            SchemeInfo info = entry.getValue();
            if(isRealScheme(info.getCode())) {
                lvls.add(new Level(info.getCode(), true));
            }
        }

        // Calcolo l'altezza (in GridDot) dell'ultimo schema.
        int totLevels = lvls.size()-1;
        int _d = totLevels / L;
        int _m = totLevels % (L * 2);
        int max = _m == 0 || _m == L ? totLevels / 2 : 1 + _d * 2;


        // Calcolo le posizioni.
        int i = 0;
        int xO = 1, yO = 2;
        int x = 0, y = 0;
        boolean prevLevelUnlocked = false;
        for(Level l : lvls) {
            Segment segmentToAdd = null;
            int d = i / L;
            int m = i % (L * 2);

            // Calcolo lo stato.
            l.setListener(this);
            if(l.isSavedAsUnlocked()){
                l.setUnlocked(true);
            } else {
                l.setUnlocked(l.checkLevelRequirement());
            }

            // Calcolo del path (Parte 1) e aggiornamento di "prevLevelUnlocked"
            if(prevLevelUnlocked && l.isUnlocked()){
                // In questo momento x e y si riferiscono al precedente schema
                segmentToAdd = new Segment(new GridDot(x,y), new GridDot(0,0));
            }
            prevLevelUnlocked = l.isUnlocked();


            // Calcolo della curva: Questo fa la linea di serpente
            x = (m <= L) ? m : (L * 2 - m);
            y = m == 0 || m == L ? i / 2 : 1 + d * 2;

            // Calcolo dell'offset:
            x = x + xO;
            y = max - y + yO;

            // Calcolo del path (Parte 2)
            if(segmentToAdd != null){
                segmentToAdd.theta = (new GridDot(x,y)).pixelDot();
                path.add(segmentToAdd);
            }

            // Salvo la posizione dell'ultimo livello sbloccato
            if(l.isUnlocked()){
                lastScrollValue = (int) (new GridDot(x, y).pixelY());
            }

            l.setPosition(new GridDot(x, y));
            i++;
        }

        setHeight(new GridDot(LVL_PER_LINE+1, max + yO*2));
        scrollTo(lastScrollValue - Consts.H/2);

    }

    private void scrollTo(int value) {
        scrollValue = Utils.bounds(value,0,height);
    }

    private boolean isRealScheme(String code) {
        return Utils.isNumeric(code);
    }


    public List<Level> getLevels() {
        return lvls;
    }

    @Override
    public void onTap(String id) {
        MainActivity.play(getActivity(), id);
    }

    private class DissipatorRunnable implements Runnable {
        private float velocity = 0;

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        @Override
        public void run() {
            float vValue = velocity;
            if(velocity > 0) {
                while (vValue > 0 && scrollValue > 0) {
                    scrollValue = Utils.bounds((int) (scrollValue - vValue), 0, height);
                    vValue -= 5;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                while (vValue < 0  && scrollValue < height) {
                    scrollValue = Utils.bounds((int) (scrollValue - vValue), 0, height);
                    vValue += 5;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
