package com.corazza.fosco.lumenGame.gameObjects;

import android.view.MotionEvent;
import android.view.View;

import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Radical;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Phase;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.MenuSchemeLayout;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

/**
 * Created by Simone Chelo on 16/11/2016.
 */
public class SegmentCreatorListener implements View.OnTouchListener {
    private SchemeLayout layout;
    private boolean active;
    private Line onBuildLine;

    public SegmentCreatorListener(SchemeLayout layout) {
        this.layout = layout;
        this.active = true;
    }

    public SegmentCreatorListener(SchemeLayout layout, boolean active) {
        this.layout = layout;
        this.active = active;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(active) {
            boolean hud1Touched = layout.hud1.onTouch(event);
            boolean hud2Touched = layout.hud2.onTouch(event);
            if (layout.getPhase() != Phase.USER_PLAYING || !layout.isTouchEnabled() || hud1Touched || hud2Touched) {
                if (!hud2Touched && layout.getPhase() == Phase.RESULT) {
                    // This means that I touched something on the result screen!
                    layout.onNextButtonClick();
                }
                return true;
            }

            view.getParent().requestDisallowInterceptTouchEvent(true);
            Dot rawPoint = new PixelDot(event.getRawX(), event.getRawY());
            Dot normPoint = layout.grid.nearest(rawPoint);

            PrecisionLineBuilding(normPoint, event.getAction(), view);

            layout.updateMinDist();

            return true;
        }
        return false;
    }

    private void PrecisionLineBuilding(Dot dot, int action, View view) {

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initGammaPoint(dot);
                break;

            case MotionEvent.ACTION_MOVE:
                //Creo il punto Theta del Segmento

                if (onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_MOVE, dot, view);
                }
                break;

            case MotionEvent.ACTION_UP:

                if (onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_UP, dot, view);
                }
                break;

        }
    }

    private void FastLineBuilding(Dot rawDot, Dot griDot, int action, View view) {

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initGammaPoint(griDot);
                break;

            case MotionEvent.ACTION_MOVE:
                //Creo il punto Theta del Segmento

                if(onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_MOVE, griDot, view);
                    if(onBuildLine instanceof Segment){
                        // Ecco tutte le condizioni del "Fast"
                        Segment s = (Segment) onBuildLine;
                        boolean superNear = griDot.pixelDistance(rawDot) < 15;
                        boolean ntOnStart = !griDot.equals(s.gamma);
                        boolean shortSegm = s.length().LessOrEqualTo(Radical.Rad2) ;
                        boolean illegalPs = layout.illegalPosition((Segment) onBuildLine);
                        if (superNear && ntOnStart /*&& shortSegm*/ && !illegalPs) {
                            onBuildLine = s.lineCreate(MotionEvent.ACTION_UP, griDot, view);
                            initGammaPoint(griDot);
                        }
                    }
            }
                break;

            case MotionEvent.ACTION_UP:

                if(onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_UP, griDot, view);
                }
                break;

        }
    }

    private void initGammaPoint(Dot dot) {
        Segment segmIamBui = new Segment(dot, dot);
        onBuildLine = layout.getPath().add(segmIamBui);
    }
}
