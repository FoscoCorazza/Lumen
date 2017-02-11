package com.corazza.fosco.lumenGame.helpers;

/**
 * Created by Simone on 06/02/2017.
 */

public class Palette {



    public enum Gradiation {

        LUMOUS(4),
        BRIGHT(3),
        NORMAL(2),
        GLOOMY(1),
        DARKKK(0);

        private final int value;

        Gradiation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static Palette get(){
        return BRIGHT;
    }

    private int[] back;
    private int[] main;
    private int[] anti;

    private int nega;
    private int hipo;
    private int defl;

    private Palette(int[] back, int[] main, int[] anti, int nega, int hipo, int defl) {
        this.back = back;
        this.main = main;
        this.anti = anti;
        this.nega = nega;
        this.hipo = hipo;
        this.defl = defl;
    }

    public int getBack(Gradiation gradiation){ return back[gradiation.getValue()];}
    public int getMain(Gradiation gradiation){ return main[gradiation.getValue()];}
    public int getAnti(Gradiation gradiation){ return anti[gradiation.getValue()];}

    public int getNega() {
        return nega;
    }

    public int getHipo() {
        return hipo;
    }

    public int getDefl() {
        return defl;
    }

    private static Palette STANDARD = new Palette(
            new int[]{ Colors.BLACK, Colors.GREY_DARKEST, Colors.GREY_DARK, Colors.GREY, Colors.GREY_LIGHT },
            new int[]{ 0, Colors.GREEN_DARK, Colors.GREEN, 0 , 0 },
            new int[]{ Colors.GREY_LIGHTEST, Colors.WHITE_DARKEST, Colors.WHITE_DARK, Colors.WHITE, Colors.WHITE_LIGHT },
            Colors.RED, Colors.YELLOW, Colors.BLUE
    );

    private static Palette BRIGHT = new Palette(
            new int[]{  Colors.WHITE_DARKEST, Colors.WHITE_DARK,  Colors.WHITE, Colors.WHITE_LIGHT, Colors.WHITE_LIGHTEST },
            new int[]{ 0, Colors.GREEN_DARK, Colors.GREEN, 0 , 0 },
            new int[]{ Colors.BLACK, Colors.GREY_DARKEST, Colors.GREY_DARK, Colors.GREY, Colors.GREY_LIGHT },
            Colors.RED, Colors.YELLOW, Colors.BLUE
    );

    private static Palette BLUETTE = new Palette(
            new int[]{  Colors.BLUETTE_DARKEST, Colors.BLUETTE_DARK,  Colors.BLUETTE, Colors.BLUETTE_LIGHT, Colors.BLUETTE_LIGHTEST },
            new int[]{ 0, Colors.BLUETTE_DARK, Colors.BLUE, 0 , 0 },
            new int[]{ Colors.BLACK, Colors.GREY_DARKEST, Colors.GREY_DARK, Colors.GREY, Colors.GREY_LIGHT },
            Colors.RED, Colors.YELLOW, Colors.BLUE
    );


    private class Colors {
         static final int BLACK = 0XFF242A2E;
         static final int RED = 0xFFf44336;
         static final int BLUE = 0xFF3164F7;
         static final int YELLOW = 0xFFF4E436;

        static final int GREY_DARKEST = 0xff212829;
        static final int GREY_DARK = 0xff283232;
        static final int GREY = 0xff323c3c;
        static final int GREY_LIGHT = 0xFF3C4646;
        static final int GREY_LIGHTEST = 0xFF4C5656;

        static final int WHITE_DARKEST = 0xffC3B9B9;
        static final int WHITE_DARK = 0xffCDC3C3;
        static final int WHITE = 0xffD7CDCD;
        static final int WHITE_LIGHT = 0XFFDBD5D1;
        static final int WHITE_LIGHTEST = 0xffDED7D6;

        static final int BLUETTE_DARKEST = 0xffB3A9E9;
        static final int BLUETTE_DARK = 0xffBDC3E3;
        static final int BLUETTE = 0xffC7BDFD;
        static final int BLUETTE_LIGHT = 0XFFCBC5FF;
        static final int BLUETTE_LIGHTEST = 0xffCEC7FF;

        static final int GREEN = 0xFF4CAF50;
        static final int GREEN_DARK = 0xFF388E3C;
    }

}
