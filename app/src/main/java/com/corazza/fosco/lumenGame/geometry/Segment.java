package com.corazza.fosco.lumenGame.geometry;

import android.graphics.*;
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

        if(getDrawingSettings().showStrike) canvas.drawLine(gamma.pixelX(), gamma.pixelY(), theta.pixelX(), theta.pixelY(), backPaint);
        canvas.drawLine(gamma.pixelX(), gamma.pixelY(), theta.pixelX(), theta.pixelY(), mainPaint);
        drawLineTerminators(canvas, r);
        if(getDrawingSettings().showString && isShowLength()) drawQuantity(canvas);
    }

    protected void drawLineTerminators(Canvas canvas, float r) {
        canvas.drawCircle(gamma.pixelX(), gamma.pixelY(), r, getPaint(MAINPAINT));
        canvas.drawCircle(theta.pixelX(), theta.pixelY(), r, getPaint(MAINPAINT));
    }

    public void drawText(Canvas canvas, String s){
        Float angle = (float) (Math.PI/2 - Math.atan((theta.pixelX() - gamma.pixelX()) /(theta.pixelY() - gamma.pixelY())));
        angle = (float)(180 / Math.PI) * angle;
        angle = angle > 90 ? angle + 180 : angle;
        float x = center().pixelX();
        float y = center().pixelY() - 10;
        canvas.save();
        canvas.rotate(angle, center().pixelX(), center().pixelY());
        canvas.drawText(s, x, y, getPaint(TEXTPAINT, textOpacity));
        canvas.restore();
    }


    private void drawQuantity(Canvas canvas) {
        Float angle = (float) (Math.PI/2 - Math.atan((theta.pixelX() - gamma.pixelX()) /(theta.pixelY() - gamma.pixelY())));
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

    public float top()   { return (float) Math.min(gamma.pixelY(), theta.pixelY()); }
    public float left()  { return (float) Math.min(gamma.pixelX(), theta.pixelX()); }
    public float right() { return (float) Math.max(gamma.pixelX(), theta.pixelX()); }
    public float bottom(){ return (float) Math.max(gamma.pixelY(), theta.pixelY()); }

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
                    setColor(Consts.Colors.WHITE);
                }

                break;

            case MotionEvent.ACTION_UP:
                if (!length().isZero() && schemeLayout.path.contains(this) < 2 && !obstructed) {
                    List<Line> splitted = schemeLayout.path.splitAt(this, schemeLayout.getMainLumen().position);
                    for(Bulb bulb : schemeLayout.getBulbs()) {
                        splitted = schemeLayout.path.splitAt(splitted, bulb.position);
                    }
                    schemeLayout.path.splitAt(splitted, schemeLayout.grid);
                    return null;
                } else {
                    schemeLayout.path.remove(this);
                }
                setColor(Consts.Colors.WHITE);
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


}
