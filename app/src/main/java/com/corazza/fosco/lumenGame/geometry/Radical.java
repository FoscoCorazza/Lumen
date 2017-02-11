package com.corazza.fosco.lumenGame.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.SparseIntArray;

import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;

import java.util.ArrayList;
import java.util.Locale;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

/**
 * Created by Simone Chelo on 05/11/2016.
 */
public class Radical implements Comparable<Radical>{

    private boolean isInfinite = false;
    public static final Radical Infinite = new Radical(true);
    public static final Radical Zero = new Radical();
    public static final Radical One = new Radical(new Segment(GridDot.Zero, new GridDot(0,1)));
    public static final Radical Rad2 = new Radical(new Segment(GridDot.Zero, GridDot.One));

    // È una Hashmap<Int, Int>, niente di che.
    private SparseIntArray components = new SparseIntArray();
    private static boolean paintInited = false;

    public Radical(Radical radical) {
        add(radical);
    }

    public Radical() { }

    private Radical(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    public Radical(Segment s) {
        int c1 = s.theta.gridX() - s.gamma.gridX();
        int c2 = s.theta.gridY() - s.gamma.gridY();
        int hy = c1*c1 + c2*c2;
        int simpleRadq = (int) Math.sqrt(hy);
        int m = 1;
        for(int i = 2; i<=simpleRadq; i++){
            int d = i*i;
            if(hy % d == 0) {
                m*=i;
                hy = hy / d;
                i = 1;
                simpleRadq = (int) Math.sqrt(hy);
            }
        }
        addComponent(hy, m);
    }

    private double asDouble() {
        double d = 0;
        for(int i = 0; i < components.size(); i++) {
            int rad = components.keyAt(i);
            int mul = components.get(rad);
            d += mul * Math.sqrt(rad);
        }
        return d;
    }

    public Radical add(Radical radical) {
        SparseIntArray comp2 = radical.getComponents();
        for(int i = 0; i < comp2.size(); i++) {
            addComponent(comp2.keyAt(i), comp2.valueAt(i));
        }
        return this;
    }

    private void addComponent(int rad, int mul) {
        int myMul = components.get(rad);
        components.put(rad, mul + myMul);
    }

    public Radical sum(Radical radical) {
        Radical r = new Radical(this);
        return r.add(radical);
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public SparseIntArray getComponents() {
        return components;
    }

    public void setComponents(SparseIntArray components) {
        this.components = components;
    }

    public boolean isZero() {
        for(int i = 0; i < components.size(); i++) {
            int rad = components.keyAt(i);
            if(rad != 0 && components.valueAt(i) > 0) return false ;
        }
        return true;
    }


    @Override
    public int compareTo(@NonNull Radical radical) {
        if(radical.isInfinite()) return isInfinite() ? 0 : -1;
        if(isInfinite()) return 1;
        if(equals(radical)) return 0;
        return asDouble() < radical.asDouble() ? -1 : 1;
    }

    public boolean LessOrEqualTo(Radical radical){
        return compareTo(radical) <= 0;
    }

    public boolean LessThen(Radical radical){
        return compareTo(radical) < 0;
    }

    public String toString(boolean radicalMode) {
        if(isInfinite()) return "∞";
        if(!radicalMode){
            return String.format(Locale.US, "%.2f", asDouble());
        } else {
            String s = "";
            for(int i = 0; i < components.size(); i++) {
                int rad = components.keyAt(i);
                int mul = components.get(rad);
                double sqrt = Math.sqrt(rad);
                if(Math.round(sqrt) == sqrt){
                    // It is a perfect square
                    s += String.valueOf((int) sqrt * mul);
                } else {
                    // It is a radical
                    String m = mul == 1 ? "" : String.valueOf(mul);
                    s += m + "√" + String.valueOf(rad);
                }
                s += " +";
            }
            return s.isEmpty() ? "0" : s.substring(0, s.length()-2);
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Radical radical = (Radical) o;
        if(components == null) return radical.components == null;
        if(isInfinite()) return radical.isInfinite();
        return Math.abs(pixelLength() - radical.pixelLength()) < 1; //TODO Fare un vero controllo

    }

    @Override
    public int hashCode() {
        return components != null ? components.hashCode() : 0;
    }

    public double pixelLength() {
        return asDouble() * Consts.baseGridSize;
    }

    public Pair<Integer, Integer> higherComponent() {
        Pair<Integer, Integer> r = new Pair<>(0,0);
        for(int i = 0; i < components.size(); i++) {
            if(components.keyAt(i) > r.first) {
                r = new Pair<>(components.keyAt(i), components.valueAt(i));
            }
        }
        return r;
    }

    // Draw stuff
    private static final String RADPAINT = "RADPAINT";
    
    protected void initPaints() {
        if(!paintInited) {
            paintInited = true;
            Paints.put(RADPAINT + 1,  0x00bbd3, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 2,  0xe81d62, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 5,  0xfe9700, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 10, 0xfe5621, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 13, 0x785447, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 17, 0x009587, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 26, 0x363f45, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 29, 0x9b26af, Consts.lineW, Paint.Style.FILL);

            Paints.put(RADPAINT + 34, 0x3e50b4, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 37, 0xe81d62, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 41, 0x009587, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 45, 0xfe9700, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 53, 0x9b26af, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 58, 0x9b26af, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 61, 0xfe5621, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 65, 0x785447, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 73, 0xfe9700, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 74, 0x9b26af, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 85, 0x363f45, Consts.lineW, Paint.Style.FILL);
            Paints.put(RADPAINT + 89, 0x363f45, Consts.lineW, Paint.Style.FILL);
        }
    }

    private Paint getColoredPaint(int component, int alpha) {
        initPaints();
        return Paints.get(RADPAINT + component, alpha);
    }

    public void drawOnCanvas(Canvas canvas, int x, int y, Paint backpaint) {
        int pw = scaledFrom480Int(4);
        int sw = scaledFrom480Int(10);
        ArrayList<Integer> componentsArray = componentsArray();
        int n = componentsArray.size();
        if(!equals(Zero)) {
            for (int i = 0; i < n; i++) {
                int cx = x + (i - n / 2) * sw + sw / 2;
                if (n % 2 == 1) {
                    cx = x + (i - n / 2) * sw;
                }
                canvas.drawCircle(cx, y, pw+2, backpaint);
                canvas.drawCircle(cx, y, pw, getColoredPaint(componentsArray.get(i), backpaint.getAlpha()));
            }
        }
    }

    private ArrayList<Integer> componentsArray() {
        ArrayList<Integer> r = new ArrayList<>();
        for(int i = 0; i < components.size(); i++) {
            for (int j = 0; j < components.valueAt(i); j++) {
                r.add(components.keyAt(i));
            }
        }
        return r;
    }
}
