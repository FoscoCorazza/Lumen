package com.corazza.fosco.lumenGame.gameObjects;

import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Radical;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Phase;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

/**
 * Created by Simone Chelo on 16/11/2016.
 */
public class SegmentCreatorListener implements View.OnTouchListener {
    private SchemeLayout layout;
    private boolean active;
    private Line onBuildLine;
    private VelocityTracker mVelocityTracker;
    private Integer actualVelocity = 0;

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
                    active = false;
                }
                return true;
            }

            view.getParent().requestDisallowInterceptTouchEvent(true);
            Dot rawPoint = new PixelDot(event.getRawX(), event.getRawY());
            Dot normPoint = layout.grid.nearest(rawPoint);

            FastLineBuilding(rawPoint, normPoint, event.getAction(), event, view);

            layout.updateMinDist();

            if(wait > 10) {
                wait = 0;
                layout.setDebugLabel(actualVelocity);
            }
            wait++;

            return true;
        }
        return false;
    }
    private int wait = 0;

    private void PrecisionLineBuilding(Dot dot, int action, MotionEvent event, View view) {

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initGammaPoint(dot);
                initVelocityTracker(event);
                break;

            case MotionEvent.ACTION_MOVE:
                //Creo il punto Theta del Segmento

                if (onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_MOVE, dot, view);
                }
                updateVelocity(event);
                break;

            case MotionEvent.ACTION_UP:

                if (onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_UP, dot, view);
                }
                break;

        }
    }



    private void FastLineBuilding(Dot rawDot, Dot griDot, int action, MotionEvent event, View view) {

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initGammaPoint(griDot);
                initVelocityTracker(event);
                break;

            case MotionEvent.ACTION_MOVE:
                //Creo il punto Theta del Segmento

                updateVelocity(event);
                if(onBuildLine != null) {
                    onBuildLine = onBuildLine.lineCreate(MotionEvent.ACTION_MOVE, griDot, view);
                    if(onBuildLine instanceof Segment){
                        // Ecco tutte le condizioni del "Fast"
                        Segment s = (Segment) onBuildLine;
                        boolean superNear = griDot.pixelDistance(rawDot) < toleranceByVelocity();
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

    private int toleranceByVelocity() {
        if(actualVelocity != null) {
            if(actualVelocity > 400)   return Utils.scaledInt(50);
            if(actualVelocity > 200)   return Utils.scaledInt(40);
        }
        return Utils.scaledInt(20);
    }

    private void initGammaPoint(Dot dot) {
        Segment segmIamBui = new Segment(dot, dot);
        onBuildLine = layout.getPath().add(segmIamBui);
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
        if(mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
            mVelocityTracker.computeCurrentVelocity(1000);
            PixelDot pDot = new PixelDot(
                    VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId),
                    VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId));
            actualVelocity = (int) Utils.hypotenuse(pDot.pixelX(), pDot.pixelY());
            return pDot;
        }
        return new PixelDot(0,0);
    }

}
