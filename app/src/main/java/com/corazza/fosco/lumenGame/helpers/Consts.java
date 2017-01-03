package com.corazza.fosco.lumenGame.helpers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
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

import java.util.HashMap;

import static com.corazza.fosco.lumenGame.helpers.Utils.scaled;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledInt;

public class Consts {

    public static final boolean DEMO = true;

    //Sizes
    public static int    baseGridSize;
    public static int    hStep = 8;
    public static int    vStep = 8;
    public static float  lineW;
    public static int    dotSize;
    public static double lumenSpeed;
    public static int    lumenSize;
    public static float  lineTerminatorRadius;

    public static String TITLE = "LUMEN";
    public static String PRETITLE = "Fosco Corazza's";

    public static String EndLevelString1 = "EndLevelString1";
    public static String EndLevelString2 = "EndLevelString2";

    public static final float INFINITE   = 1993014007;
    public static final Dot COINCIDENT = new PixelDot(1993, 1407);

    // System
    private static Point screenSize = new Point(0,0);
    public static int lumenMax = 100;
    public static int W, H;
    public static Typeface detailFont;
    public static HashMap<String, SchemeInfo> schemeList;
    public static HashMap<String, MenuSchemeInfo> menuSchemeList;

    public  static void loadConsts(Context ctx){
        detailFont = Typeface.createFromAsset(ctx.getAssets(), "HelveticaNeue-Thin.otf");
        if(new Point(0,0).equals(screenSize)){
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getSize(screenSize);
            baseGridSize = screenSize.x/hStep;
            vStep = screenSize.y/baseGridSize;
        }
        W = screenSize.x;
        H = screenSize.y;

        lineW = scaled(2);
        dotSize = scaledInt(1);
        lumenSize = scaledInt(10);
        lumenSpeed = scaled(0.3f);
        lineTerminatorRadius = scaled(3);

        if(schemeList == null)
            schemeList = SchemeReader.read(ctx);
        if(menuSchemeList == null)
            menuSchemeList = SchemeReader.readMenu(ctx);
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


    public class Colors {
        public static final int WHITE = 0xFFFFFFFF;
        public static final int MATERIAL_GREY = 0xff374046;
        public static final int MATERIAL_BLACK = 0XFF242A2E;
        public static final int MATERIAL_GREEN = 0xFF4CAF50;
        public static final int MATERIAL_RED = 0xFFf44336;
        public static final int MATERIAL_BLUE = 0xFF3164F7;
        public static final int MATERIAL_YELLOW = 0xFFF4E436;
        public static final int MATERIAL_GREEN_DARK = 0xFF388E3C;
        public static final int MATERIAL_LIGHT_GREY = 0xFF3C4646;
        public static final int MATERIAL_LIGHTEST_GREY = 0xFF4C5656;
        public static final int MATERIAL_WHITE = 0XFFDBD5D1;
    }
}
