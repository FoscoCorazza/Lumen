package com.corazza.fosco.lumenGame.helpers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;
import com.corazza.fosco.lumenGame.savemanager.SchemeResult;
import com.corazza.fosco.lumenGame.schemes.MenuSchemeInfo;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.MenuSchemeLayout;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.schemes.SchemeReader;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.TreeMap;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480Int;

public class Consts {

    public static final boolean DEMO = true;
    public static final boolean DEBUG = true;

    //Sizes
    public static int    baseGridSize;
    public static int    hStep = 6;
    public static int    vStep = 6;
    public static float  lineW;
    public static int    dotSize;
    public static double lumenSpeed;
    public static int    lumenSize;
    public static float  lineTerminatorRadius;

    public static String TITLE = "LUMEN";
    public static String PRETITLE = "Fosco Corazza's";


    public static final float INFINITE   = 1993014007;
    public static final Dot COINCIDENT = new PixelDot(1993, 1407);

    // System
    private static Point screenSize = new Point(0,0);
    public static int lumenMax = 100;
    public static int W, H;
    public static Typeface detailFont;
    public static TreeMap<String, SchemeInfo> schemeList;
    public static TreeMap<String, MenuSchemeInfo> menuSchemeList;

    public  static void loadConsts(Context ctx){
        detailFont = Typeface.createFromAsset(ctx.getAssets(), "HelveticaNeue-Thin.otf");
        if(new Point(0,0).equals(screenSize)){
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.getDefaultDisplay().getRealSize(screenSize);
            }else {
                try {
                    screenSize.x = (Integer) Display.class.getMethod("getRawWidth").invoke(wm);
                    screenSize.y = (Integer) Display.class.getMethod("getRawHeight").invoke(wm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            baseGridSize = screenSize.x/hStep;
            vStep = screenSize.y/baseGridSize;
        }
        W = screenSize.x;
        H = screenSize.y;

        lineW = scaledFrom480(2);
        dotSize = scaledFrom480Int(1);
        lumenSize = scaledFrom480Int(10);
        lumenSpeed = scaledFrom480(0.3f);
        lineTerminatorRadius = scaledFrom480(3);

        if(schemeList == null)
            schemeList = SchemeReader.read(ctx);
        if(menuSchemeList == null)
            menuSchemeList = SchemeReader.readMenu(ctx);

        Paints.initConstPaints();
    }

    public static SchemeLayout getSchemeLayout(String name, SchemeLayout base){
        SchemeInfo info = schemeList.get(name);
        return info == null ? null : info.toSchemeLayout(base);
    }

    public static void loadProgresses(Context context) {
        // TODO: Salvando in appPreferences l'ultimo salvataggio
        // e l'ultimo read posso velocizzare questa funzione.

        HashMap<String, SchemeResult> progress = SaveFileManager.readFile(context);
        if(schemeList != null) {
            for (SchemeInfo scheme : schemeList.values()) {
                scheme.setResult(progress.get(scheme.getCode()));
            }
        }
    }

    public static String getString(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        return resId > 0 ? c.getString(resId) : null;
    }

    public static MenuSchemeLayout getMenuSchemeLayout(String name, MenuSchemeLayout base) {
        MenuSchemeInfo info = menuSchemeList.get(name);
        return info == null ? null : info.toMenuSchemeLayout(base);
    }

    public static void updateSchemeList(SchemeResult result) {
        SchemeInfo schemeInfo = schemeList.get(result.getCode());
        if(schemeInfo != null){
            schemeInfo.setResult(result);
        }
    }

}
