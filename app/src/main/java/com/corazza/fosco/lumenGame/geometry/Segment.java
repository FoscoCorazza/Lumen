package com.corazza.fosco.lumenGame.geometry;

import android.graphics.*;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.corazza.fosco.lumenGame.comparators.DotCompare;
import com.corazza.fosco.lumenGame.gameObjects.Bulb;
import com.corazza.fosco.lumenGame.gameObjects.Lumen;
import com.corazza.fosco.lumenGame.gameObjects.obstacles.Obstructor;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class Segment extends Rect {

    /* Il Sistema Gamma-Theta
    *
    * I due punti del segmento sono chiamati in questo modo pe rnon far presupporr che uno sia pre-
    * cedente all'altro. Per inizializzare un segmento non è importante l'ordine dei punti, ma per
    * il movimento si: ecco perchè le funzioni getEnd() e getBegin() vogliono una direzione.
    *
    * */

    public Segment(Dot gamma, Dot theta) {
        super(gamma, theta);
    }

    public Segment(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    // Disegno

    @Override
    protected void initPaints() {
        super.initPaints();
    }

    @Override
    public void render(Canvas canvas){

        float r = Consts.lineTerminatorRadius;

        Paint mainPaint = getPaint(MAINPAINT);
        Paint backPaint = getPaint(BACKPAINT);
        int gx = (int) (gamma.pixelX() + offset.pixelX());
        int gy = (int) (gamma.pixelY() + offset.pixelY());
        int tx = (int) (theta.pixelX() + offset.pixelX());
        int ty = (int) (theta.pixelY() + offset.pixelY());


        if(getDrawingSettings().showStrike) canvas.drawLine(gx, gy, tx, ty, backPaint);
        canvas.drawLine(gx, gy, tx, ty, mainPaint);
        drawLineTerminators(canvas, gx, gy, tx, ty, r);
        if(getDrawingSettings().showString && isShowLength()) drawQuantity(canvas, gx, gy, tx, ty);
    }

    @Override
    public void render(Canvas canvas, int x, int y) {
        offset.change(x,y);
        render(canvas);
    }

    protected void drawLineTerminators(Canvas canvas, int gx, int gy, int tx, int ty, float r) {
        canvas.drawCircle(gx, gy, r, getPaint(MAINPAINT));
        canvas.drawCircle(tx, ty, r, getPaint(MAINPAINT));
    }

    private void drawQuantity(Canvas canvas, int gx, int gy, int tx, int ty) {
        Float angle = (ty - gy) == 0 ? (float)Math.PI : (float) (Math.PI/2 - Math.atan((tx - gx) /(ty - gy)));
        angle = (float)(180 / Math.PI) * angle;
        angle = angle > 90 ? angle + 180 : angle;
        int x = (int) center().pixelX();
        int y = (int) center().pixelY();
        canvas.save();
        canvas.rotate(angle, x, y);
        length().drawOnCanvas(canvas, x, y, getPaint(MAINPAINT));
        canvas.restore();
    }


    //Geometric Functions

    @Override
    public Radical length() {
        return new Radical(this);
    }

    @Override
    public boolean contains(Dot dot) {

        float left   = Math.min(gamma.pixelX(), theta.pixelX());
        float right  = Math.max(gamma.pixelX(), theta.pixelX());
        float top    = Math.min(gamma.pixelY(), theta.pixelY());
        float bottom = Math.max(gamma.pixelY(), theta.pixelY());
        float x = dot.pixelX(), y = dot.pixelY();

        boolean inTheBox = x >= left && x <= right && y >= top && y <= bottom;

        return super.contains(dot) && inTheBox;
    }

    public boolean containsAnyOf(List<Dot> dots) {
        for(Dot dot : dots)
            if(contains(dot)) return true;
        return false;
    }

    public boolean coincident(Rect rect){
        boolean s = super.coincident(rect);
        if(rect instanceof Segment && s) {
            Segment that = (Segment) rect;

            float l1 = left();
            float r1 = right();
            float l2 = that.left();
            float r2 = that.right();

            float t1 = top();
            float b1 = bottom();
            float t2 = that.top();
            float b2 = that.bottom();

            return l1 <= r2 && r1 >= l2 && t1 <= b2 && b1 >= t2;
        }
        else return s;

    }

    @Override
    public boolean startsAt(Dot point) {
        return gamma.equals(point) || theta.equals(point);
    }

    @Override
    public List<Dot> getSplitPoints(Line line) {
        ArrayList<Dot> splits = new ArrayList<>();
        if(line instanceof Segment){
            Segment that = (Segment) line;
            Dot intersecate = intersecates(that);
            if (intersecate != null && !equals(that)) {
                if (intersecate.equals(Consts.COINCIDENT)){
                    // Creo una lista ordinata di al massimo quattro punti,
                    // da quello più a sinistra a quello più a destra.
                    //ArrayList<Point> points = new ArrayList<>();

                    splits.add(this.gamma);
                    if(!splits.contains(this.theta)) splits.add(this.theta);
                    if(!splits.contains(that.gamma)) splits.add(that.gamma);
                    if(!splits.contains(that.theta)) splits.add(that.theta);

                    Collections.sort(splits, new DotCompare());
                }
                else{
                    splits.add(intersecate);
                }
            }
        }
        else{
            //TODO Circle
        }
        return splits;
    }


    /*private List<Line> splitAtThisDistance(double k) {
        double a = gamma.pixelX();
        double b = gamma.pixelY();
        double c = theta.pixelX();
        double d = theta.pixelY();

        double radix = Math.sqrt(a*a  - 2*a*c + b*b  - 2*b*d + c*c  + d*d);
        double sign  = Math.signum(b-d);
        double abs   = Math.abs(b - d);
        // Il problema è che ragiono a rette: per questo i punti che distano "unit" da gamma sono due.
        // Devo trovare l'unico chè è sul segmento.

        Point candidate1 = new Point(
                (int) ((a * radix + k * (a-c) * sign) / radix) ,
                (int) ((b * radix + k * abs) / radix) );

        Point candidate2 = new Point(
                (int) ((a * radix - k * (a-c) * sign) / radix) ,
                (int) ((b * radix - k * abs) / radix) );

        Point splitPoint = contains(candidate1) ? candidate1 : candidate2;

        ArrayList<Line> r = new ArrayList<>();
        r.add(new Segment(gamma, splitPoint));
        r.add(new Segment(splitPoint, theta));

        return r;
    }*/

    public float top()   { return Math.min(gamma.pixelY(), theta.pixelY()); }
    public float left()  { return Math.min(gamma.pixelX(), theta.pixelX()); }
    public float right() { return Math.max(gamma.pixelX(), theta.pixelX()); }
    public float bottom(){ return Math.max(gamma.pixelY(), theta.pixelY()); }

    public Dot center(){return new PixelDot((gamma.pixelX() + theta.pixelX()) / 2 , (gamma.pixelY() + theta.pixelY()) / 2);}

    public void setColor(int i) {
        getPaint(MAINPAINT).setColor(i);
    }

    @Override
    public Animation getLumenAnimation(Lumen lumen){
        lumen.direction = suggestDirection(lumen);

        float x1 = getFirst(lumen.direction).pixelX() - lumen.size / 2;
        float y1 = getFirst(lumen.direction).pixelY() - lumen.size / 2;
        float x2 = getLast(lumen.direction).pixelX()  - lumen.size / 2;
        float y2 = getLast(lumen.direction).pixelY()  - lumen.size / 2;

        return new TranslateAnimation(0, x2-x1, 0, y2-y1);

    }

    @Override
    public void onAnimationEnd(Lumen lumen) {
        lumen.setPosition(getLast(lumen.direction));
    }

    @Override
    public Line lineCreate(int action, Dot normPoint, View view) {
        if(! (view instanceof SchemeLayout) ) return null;
        SchemeLayout schemeLayout = (SchemeLayout) view;
        boolean obstructed = schemeLayout.illegalPosition(this);

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                //Creo il punto Theta del Segmento
                theta = normPoint.pixelDot();
                //theta = Utils.nearest(normPoint, thetaCandidates());
                //if(containsAnyOf(schemeLayout.getSpikesPositions())){
                if(obstructed){
                    setColor(Obstructor.getColor());
                } else {
                    setColor(Palette.get().getAnti(Palette.Gradiation.LUMOUS));
                }

                break;

            case MotionEvent.ACTION_UP:
                Path path = schemeLayout.path;
                if (!length().isZero() && path.contains(this) < 2 && !obstructed) {
                    schemeLayout.flatten();
                    schemeLayout.split();
                    return null;
                } else {
                    path.remove(this);
                }
                setColor(Palette.get().getAnti(Palette.Gradiation.LUMOUS));
                break;
        }
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Segment) {
            Segment segment = (Segment) o;
            return (segment.gamma.equals(gamma) && segment.theta.equals(theta)) ||
                   (segment.gamma.equals(theta) && segment.theta.equals(gamma));
        }
        return false;
    }

/*
    // Candidati Theta dato un certo Gamma
    public Set<PixelDot> thetaCandidates(Grid grid){
        Set<Point> candidates = new HashSet<>();
        int size = PixelDot.gridSize;

        // Orizzontale
        for (int i = 0; i <= Consts.W ; i += size)
            candidates.add(new Point(i, gamma.pixelY()));

        // Verticale
        for (int i = 0; i <= Consts.H ; i += size)
            candidates.add(new Point(gamma.pixelX(), i));

        //Creo le due diagonali
        Rect diagonal1 = new Rect(gamma, new Point(gamma.pixelX() + size, gamma.pixelY() + size));
        Rect diagonal2 = new Rect(gamma, new Point(gamma.pixelX() + size, gamma.pixelY() - size));

        //Trovo il punto iniziale
        Point candidate = diagonal1.intersecates(Consts.UPPER_BOUND);
        if (candidate == null ) candidate = diagonal1.intersecates(Consts.LEFT_BOUND);

        //Scorro la diagonale
        while(candidate.y <= Consts.H && candidate.x <= Consts.W){
            candidates.add(candidate);
            candidate = new Point(candidate.x + size, candidate.y + size);
        }

        //Trovo il punto iniziale
        candidate = diagonal2.intersecates(Consts.UPPER_BOUND);
        if (candidate == null ) candidate = diagonal2.intersecates(Consts.RIGHT_BOUND);

        //Scorro la diagonale
        while(candidate.y <= Consts.H && candidate.x >= 0){
            candidates.add(candidate);
            candidate = new Point(candidate.x - size, candidate.y + size);
        }

        return candidates;
    }
*/

    @Override
    public boolean isIn(Dot dot) {
        return  dot != null &&
                (position != null && position.equals(dot)) ||
                (gamma != null && gamma.equals(dot)) ||
                (theta != null && theta.equals(dot)) ||
                (contains(dot));
    }


    /*public List<Dot> merge(Segment segment, Segment newSegment) {
        Dot extreme1 = null;
        Dot extreme2 = null;
        List<Dot> r = new ArrayList<>();
        // Case 1: The end of one is in common.
            Pair<Dot, Dot> a = mergeCheck(this, segment, true, r);
            Pair<Dot, Dot> b = mergeCheck(this, segment, false, r);
            extreme1 = a.first != null ? (a.first) : (b.first != null ? b.first : null);
            extreme2 = a.second != null ? (a.second) : (b.second != null ? b.second : null);

        // Case 2: They overlap but no end in common.
            if(extreme1 == null && extreme2 == null) {
                if(contains(segment.gamma)){

                    r.add(segment.gamma);
                    extreme2 = segment.theta;
                    if(segment.contains(gamma)){
                        r.add(gamma);
                        extreme1 = theta;
                    } else {
                        r.add(theta);
                        extreme1 = gamma;
                    }
                }
            }

        if(extreme1 != null && extreme2 != null) {
            /*newSegment.setExtremes(extreme1, extreme2);
            Radical sumLen = length().add(segment.length());
            if(newSegment.length().equals(sumLen)){
                return r;
            }
            if(onTheSameRectOf(segment)) {
                newSegment.setExtremes(extreme1, extreme2);
                return r;
            }
        }
        return null;
    }*/

    private Pair<Dot, Dot> mergeCheckCommonEnd(Segment s1, Segment s2, boolean gammatheta) {

        List<Dot> mergePoint = new ArrayList<>();
        Dot extreme1 = null;
        Dot extreme2 = null;

        Dot gamma0 = gammatheta ? s1.theta : s1.gamma;
        Dot theta0 = gammatheta ? s1.gamma : s1.theta;
        Dot gamma1 = gammatheta ? s2.gamma : s2.theta;
        Dot theta1 = gammatheta ? s2.theta : s2.gamma;

        if(s1.startsAt(gamma0)){
            extreme1 = theta0;
            extreme2 = gamma0.equals(gamma1) ? theta1 : gamma1;
            if(s1.contains(extreme1)){
                // Ok, segment is contained in this.
                mergePoint.add(extreme1);
                extreme1 = gamma0;
            } else if (s2.contains(extreme2)){
                mergePoint.add(extreme2);
                extreme2 = theta1;
            } else {
                mergePoint.add(gamma0);
            }
        }
        return new Pair<>(extreme1, extreme2);
    }


    public Segment merge(Segment that) {

        // Case -1: They are not on the same Rect.
        if(!onTheSameRectOf(that)) return null;

        Dot gamma0 = gamma;
        Dot theta0 = theta;
        Dot gamma1 = that.theta;
        Dot theta1 = that.gamma;

        Dot extreme1 = null;
        Dot extreme2 = null;

        // Case 0: One is inside the other.
        if(this.contains(gamma1) && this.contains(theta1)) {
            return this;
        } else if (that.contains(gamma0) && that.contains(theta0)){
            return that;
        }

        // Case 1: The end of one is in common.
        if(this.startsAt(gamma1)){
            extreme1 = theta1;
            extreme2 = gamma0.equals(gamma1) ? theta0 : gamma0;
        }
        if(this.startsAt(theta1)){
            extreme1 = gamma1;
            extreme2 = theta0.equals(theta1) ? gamma0 : theta0;
        }

        // Case 2: They overlap but no end in common.
        if(extreme1 == null && extreme2 == null) {
            if(this.contains(gamma1)){
                extreme2 = theta1;
                if(that.contains(gamma0)){
                    extreme1 = theta0;
                } else {
                    extreme1 = gamma0;
                }
            }
            if(this.contains(theta1)){
                extreme2 = gamma1;
                if(that.contains(theta0)){
                    extreme1 = gamma0;
                } else {
                    extreme1 = theta0;
                }
            }
        }

        if(extreme1 != null && extreme2 != null) {
            return new Segment(extreme1, extreme2);
        }
        return null;
    }

    private void setExtremes(Dot gamma, Dot theta) {
        this.gamma = gamma.pixelDot();
        this.theta = theta.pixelDot();
    }
}
