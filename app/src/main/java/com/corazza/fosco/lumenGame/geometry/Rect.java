package com.corazza.fosco.lumenGame.geometry;

import android.graphics.*;
import android.view.View;
import android.view.animation.Animation;

import com.corazza.fosco.lumenGame.gameObjects.Lumen;
import com.corazza.fosco.lumenGame.gameObjects.obstacles.Obstructor;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Palette;

import java.util.ArrayList;
import java.util.List;

public class Rect extends Line {
    protected static final String BACKPAINT = "SEGMNBACKPNT";
    protected static final String MAINPAINT = "SEGMNMAINPNT";
    protected static final String TEXTPAINT = "SEGMNTTEXTPNT";

    /* Il Sistema Gamma-Theta
    *
    * I due punti del segmento sono chiamati in questo modo pe rnon far presupporr che uno sia pre-
    * cedente all'altro. Per inizializzare un segmento non è importante l'ordine dei punti, ma per
    * il movimento si: ecco perchè le funzioni getEnd() e getBegin() vogliono una direzione.
    *
    * */

    public PixelDot gamma;
    public PixelDot theta;

    // Costruttori
    public Rect(Dot gamma, Dot theta){
        this.gamma = gamma.pixelDot();
        this.theta = theta.pixelDot();
        initPaints();
    }

    public Rect(int x1, int y1, int x2, int y2){
        this.gamma = new PixelDot(x1, y1);
        this.theta = new PixelDot(x2, y2);
        initPaints();
    }


    protected String getPaintPrefix() { return "SEGMENT";}

    protected Paint getPaint(String paintName) {
        return Paints.get(getPaintPrefix() + paintName, alpha());
    }

    protected Paint getPaint(String paintName, float opacity) {
        return Paints.get(getPaintPrefix() + paintName, extAlpha((int) (opacity*255)));
    }

    @Override
    protected void initPaints() {
        Paints.put(getPaintPrefix() + BACKPAINT, Palette.get().getBack(Palette.Gradiation.DARKKK), Consts.lineW +2, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + MAINPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS), Consts.lineW, Paint.Style.FILL);
        Paints.put(getPaintPrefix() + TEXTPAINT, Palette.get().getAnti(Palette.Gradiation.LUMOUS),16, Consts.detailFont);
    }


    public void render(Canvas canvas){
        canvas.drawCircle(gamma.pixelX(), gamma.pixelY(), Consts.lineTerminatorRadius, getPaint(MAINPAINT));
        canvas.drawCircle(theta.pixelX(), theta.pixelY(), Consts.lineTerminatorRadius, getPaint(MAINPAINT));
        canvas.drawLine(gamma.pixelX(), gamma.pixelY(), theta.pixelX(), theta.pixelY(), getPaint(MAINPAINT));
    }

    //Geometric Functions

    @Override
    public Radical length() {
        return Radical.Infinite;
    }

    @Override
    public boolean contains(Dot dot) {

        float tol = 0.1f;
        float m = m();
        float q = q();
        float x = dot.pixelX();
        float y = dot.pixelY();

        if(m != Consts.INFINITE) return m * x + q > (y - tol) && m * x + q < (y + tol);
        else return Math.abs(x - gamma.pixelX()) < tol;

    }

    @Override
    public Animation getLumenAnimation(Lumen lumen) {
        return null;
    }

    @Override
    public void onAnimationEnd(Lumen lumen) {

    }

    @Override
    public Line lineCreate(int action, Dot normPoint, View schemeLayout) {
        return this;
    }

    @Override
    public boolean startsAt(Dot point) {
        return false;
    }

    @Override
    public List<Dot> getSplitPoints(Line line) {
        // TODO, bisogna creare la semiretta
        return new ArrayList<>();
    }

    public boolean parallelAt(Rect rect){
        return rect.m() == m();
    }

    public boolean onTheSameRectOf(Rect rect){
        return parallelAt(rect) && rect.q() == q();
    }

    public boolean coincident(Rect rect){
        return onTheSameRectOf(rect);
    }

    public static Dot intersecates(Rect rect1, Rect rect2){
        if(rect1.parallelAt(rect2)) {
            if(rect1.coincident(rect2)){
                if(rect1 instanceof Segment && rect2 instanceof Segment){
                    Segment s1 = (Segment) rect1;
                    Segment s2 = (Segment) rect2;
                    if(s1.extensionOf(s2)) {
                        if (s1.gamma.equals(s2.gamma) || s1.gamma.equals(s2.theta)) {
                            return s1.gamma;
                        }
                        if (s1.theta.equals(s2.gamma) || s1.theta.equals(s2.theta)) {
                            return s1.theta;
                        }
                    } else {
                        return Consts.COINCIDENT;
                    }
                }
            }
            return null;
        }

        float x, y;
        float m1 = rect1.m();
        float m2 = rect2.m();
        float q1 = rect1.q();
        float q2 = rect2.q();

        //Calcolo la x
        if(m1 == Consts.INFINITE) x = rect1.gamma.pixelX();
        else if(m2 == Consts.INFINITE) x = rect2.gamma.pixelX();
        else x = (q2 - q1) / (m1 - m2);

        //Calcolo la y
        if(m2 == Consts.INFINITE) y = x * m1 + q1;
        else y = x * m2 + q2;

        if(rect1.contains(new PixelDot(x, y)) && rect2.contains(new PixelDot(x, y))) return new PixelDot(x, y);

        return null;
    }

    public Dot intersecates(Rect rect){
        return intersecates(this, rect);
    }

    public boolean intersecatesAnyOf(List<? extends Obstructor> obs){

        for(Obstructor o : obs){
            if(o.intersecates(this) != null) return true;
        }
        return false;
    }

    public float mInDegrees() {
        if(m() == Consts.INFINITE) return 90;
        return (float) Math.toDegrees(Math.atan(m()));
    }

    public float mInRadians() {
        return (float) Math.toRadians(mInDegrees());
    }

    public float m(){
        if(gamma.pixelX() == theta.pixelX()) return Consts.INFINITE;
        return (gamma.pixelY() - theta.pixelY()) / (gamma.pixelX() - theta.pixelX());
    }

    public float q(){
        Float m = m();
        if(m == Consts.INFINITE) return theta.pixelX();
        return -theta.pixelX() * m() + theta.pixelY();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Rect && !(o instanceof Segment)) {
            Rect rect = (Rect) o;
            return intersecates(rect).equals(Consts.COINCIDENT);
        }
        return false;
    }


    // Directions

    @Override
    public Direction suggestDirection(Lumen lumen){
        if(lumen.position.equals(gamma)) return Direction.GAMMATHETA;
        if(lumen.position.equals(theta)) return Direction.THETAGAMMA;
        return Direction.NOT_SPECIFIED;
    }

    @Override
    public PixelDot getFirst(Direction d){
        if(d == Direction.GAMMATHETA) return gamma;
        if(d == Direction.THETAGAMMA) return theta;
        return null;
    }

    @Override
    public PixelDot getLast(Direction d){
        if(d == Direction.GAMMATHETA) return theta;
        if(d == Direction.THETAGAMMA) return gamma;
        return null;
    }

    @Override
    public String toString() {
        return gamma.toString() + " to " + theta.toString();
    }
}