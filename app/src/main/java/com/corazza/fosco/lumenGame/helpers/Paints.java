package com.corazza.fosco.lumenGame.helpers;

import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.HashMap;

public  class Paints {
    private static HashMap<String,Paint> collection = new HashMap<>();
    public static Paint TextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public static Paint TextPaintR2L = new Paint(Paint.ANTI_ALIAS_FLAG);

    static void initConstPaints(){
        TextPaint.setTypeface(Consts.detailFont);
        TextPaint.setColor(Palette.get().getAnti(Palette.Gradiation.LUMOUS));
        TextPaint.setTextSize(Utils.scaledInt(30));
        TextPaint.setTextAlign(Paint.Align.LEFT);


        TextPaintR2L.setTypeface(Consts.detailFont);
        TextPaintR2L.setColor(Palette.get().getAnti(Palette.Gradiation.LUMOUS));
        TextPaintR2L.setTextSize(Utils.scaledInt(30));
        TextPaintR2L.setTextAlign(Paint.Align.RIGHT);
    }

    public static Paint clone(Paint paint){
        Paint r = new Paint(Paint.ANTI_ALIAS_FLAG);
        r.setTypeface(paint.getTypeface());
        r.setColor(paint.getColor());
        r.setTextSize(paint.getTextSize());
        r.setTextAlign(paint.getTextAlign());
        r.setStrokeWidth(paint.getStrokeWidth());
        r.setStyle(paint.getStyle());
        r.setAlpha(paint.getAlpha());

        return r;
    }

    public static Paint get(String id, int opacity){
        collection.get(id).setAlpha(opacity);
        return collection.get(id);
    }

    public static void put(String id, Paint paint){
        collection.put(id, paint);
    }

    public static void put(String id, int color, float width, Paint.Style style){
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(width);
        p.setColor(color);
        p.setStyle(style);
        collection.put(id, p);
    }

    public static void put(String id, int color){
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        collection.put(id, p);
    }

    public static void put(String id, int color, int size, Typeface typeface) {
        put(id, color, size, typeface, Paint.Align.CENTER);    }

    public static void put(String id, int color, int size, Typeface typeface, Paint.Align alignment) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(typeface);
        p.setColor(color);
        p.setTextSize(size);
        p.setTextAlign(alignment);
        collection.put(id, p);
    }

}
