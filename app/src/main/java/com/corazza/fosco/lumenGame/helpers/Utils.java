package com.corazza.fosco.lumenGame.helpers;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.corazza.fosco.lumenGame.helpers.Consts.W;

public class Utils {

    public static boolean isAnInt(double b){
        return  b - Math.floor(b) < 0.0001;
    }

    public static int nearest(int from, int candidate1, int candidate2){

        if(Math.abs(candidate1 - from) < Math.abs(candidate2 - from))
            return candidate1;
        else
            return candidate2;

    }

    public static double hypotenuse(float c1, float c2){
        return Math.sqrt(hypotenuseSquared(c1,c2));
    }

    public static double hypotenuseSquared(float c1, float c2){
        return c1*c1 + c2*c2;
    }


    public static double distance(Dot p1, Dot p2){
        return hypotenuse(Math.abs(p2.pixelX()-p1.pixelX()), Math.abs(p2.pixelY()-p1.pixelY()));
    }

    public static Dot nearest(Dot from, Collection<Dot> to){
        Dot min = null;
        for(Dot point : to) {
            if(min == null || distance(min, from) > distance(point, from)){
                min = point;
            }
        }
        return min;
    }

    public static float scaled(float value){
        return value * W/480f;
    }
    public static int scaledInt(int value) {
        return value * W/480;
    }



    public static void addAllIgnoreDuplicatesLines(Collection<Line> to, Collection<Line> from){
        boolean contains;
        for(Line x : from){
            contains = false;
            for(Line y : to) if(y.equals(x)) contains = true;
            if(!contains) to.add(x);
        }
    }

    public static void addAllIgnoreDuplicatesPoints(Collection<Dot> to, Collection<Dot> from){
        boolean contains;
        for(Dot x : from){
            contains = false;
            for(Dot y : to) if(y.equals(x)) contains = true;
            if(!contains) to.add(x);
        }
    }

    public static void AidLines(Collection<Line> to, Collection<Line> from){
        addAllIgnoreDuplicatesLines(to, from);
    }

    public static void AidPoints(Collection<Dot> to, Collection<Dot> from){
        addAllIgnoreDuplicatesPoints(to, from);
    }

    public static float valueOfNow(float tme, float bgnPoint, float endPoint, long bgnTime, long endTime, AnimType type) {
        long T = endTime - bgnTime;
        float t = tme;
        switch (type){
            case BOUNCER:
                float pctg = tme/endTime;
                if(pctg < 0.5f){
                    return  16*(bgnPoint - endPoint)*pctg*pctg + 10*(endPoint - bgnPoint)*pctg + bgnPoint;
                } else if (pctg < 0.75f) {
                    return  16*(endPoint - bgnPoint)*pctg*pctg + 20*(bgnPoint - endPoint)*pctg + 7 * endPoint - 6*bgnPoint;
                } else return endPoint;
            case HALFSINE:
                float sin = (float) Math.sin(tme*Math.PI/(endTime-bgnTime));
                return sin*(endPoint-bgnPoint) + bgnPoint;
            case REPEAT:
            case INFINITE:
                tme = ( tme % T ) + bgnTime;
            default:
                long C = (long) (t/T);
                if(type == AnimType.REPEAT){
                    if (C % 2 == 1) {
                        float c = bgnPoint;
                        bgnPoint = endPoint;
                        endPoint = c;
                    }
                }
                if (tme >= bgnTime && tme < endTime)
                    return bgnPoint + (tme-bgnTime)*(endPoint-bgnPoint)/(endTime-bgnTime);
                else
                    return tme < bgnTime ? bgnPoint : endPoint;
        }

    }

    public static int containsSquareBase(int base) {
        for(int i = 2; (i*i) < base; i++){
            double square = (double) base / (i*i);
            if(isAnInt(square)){
                return i;
            }
        }
        return -1;
    }
/*
    public static String simplifyRadical(String string) {
        //Questa stringa è nella forma 0+3+2√2+√5 ...

        if(string.isEmpty()) return "∞";

        String[] addendi = string.split("\\+");
        int intero = 0;
        HashMap<Integer, Integer> radicali = new HashMap<>();
        for(String addendo : addendi){
            //Può essere tre cose: un intero, un radicale o l'infinito.
            if("∞".equals(addendo)) return "∞";

            if(addendo.contains("√")){
                //Radicale
                String[] radElements = addendo.split("√");
                int radicale = 0;
                int multiplr = 1;
                //Due ulteriori casi: se radElements è lungo uno allora l'unico elemento è
                //sotto radice. Altrimenti il primo elemento è un moltiplicatore e il
                // secondo è sotto radice.
                if(radElements.length > 0) {
                    if (radElements.length < 2){
                        //No multiplier
                        radicale = Integer.parseInt(radElements[0]);
                    } else {
                        multiplr = radElements[0].isEmpty() ? 1 : Integer.parseInt(radElements[0]);
                        radicale = Integer.parseInt(radElements[1]);
                    }

                    //Ok, ora vedo se questo radicale esiste già.
                    Integer thisRadicalActualMultiplier = radicali.get(radicale);
                    if(thisRadicalActualMultiplier != null){
                        radicali.put(radicale, thisRadicalActualMultiplier + multiplr);
                    } else {
                        radicali.put(radicale, multiplr);
                    }

                }
            } else {
                //Intero
                intero += Integer.parseInt(addendo);
            }

        }

        //Ok, ora ho un'esageratissima hasmap di radicali/multiplier e un intero. Unisco.
        String radString = "";
        for (Map.Entry<Integer, Integer> r : radicali.entrySet()){
            if(!radString.isEmpty()) radString += " + ";
            if(r.getValue() > 1) radString += r.getValue();
            radString += "√" + r.getKey();
        }

        if(intero > 0)
            if(radString.isEmpty())
                return intero + "";
            else
                return intero + " + " + radString;
        else
            if(radString.isEmpty())
                return "∞";
            else
                return radString;

    }
*/
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
