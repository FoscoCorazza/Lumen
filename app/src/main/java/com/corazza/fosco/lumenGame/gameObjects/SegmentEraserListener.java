package com.corazza.fosco.lumenGame.gameObjects;

import android.view.MotionEvent;
import android.view.View;

import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.SoundsHelper;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

/**
 * Created by Simone on 15/02/2017.
 */

public class SegmentEraserListener implements View.OnTouchListener{

    private SchemeLayout schemeLayout;

    public SegmentEraserListener(SchemeLayout schemeLayout) {
        this.schemeLayout = schemeLayout;
    }

    @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (schemeLayout.touchIsUnuseful(event)) return true;
            view.getParent().requestDisallowInterceptTouchEvent(true);
            PixelDot rawPoint = new PixelDot(event.getRawX(),  event.getRawY());
            GridDot normPoint = schemeLayout.grid.nearest(rawPoint).gridDot();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    SoundsHelper.getInstance().play_eraser(schemeLayout.getActivity());
                    eraseElementAt(normPoint);
                    break;

                case MotionEvent.ACTION_UP:
                    SoundsHelper.getInstance().stop_eraser();
                    break;

            }

            return true;
        }

    protected void eraseElementAt(GridDot dot) {
        if(schemeLayout.path != null){
            Segment segm = schemeLayout.path.getSegments().elementIn(dot);
            if(segm != null) {
                schemeLayout.path.remove(segm);
                schemeLayout.flatten();
                schemeLayout.split();
            }
        }
    }

}
