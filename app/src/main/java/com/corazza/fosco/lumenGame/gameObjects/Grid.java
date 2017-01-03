package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Grid extends SchemeLayoutDrawable {

    private static final String MAINPAINT = "GRIDMAINPNT";
    private static final int CENTER = 1;
    private static final int SIZE = 0;
    private static HashSet<Dot> FILLED = new HashSet<>();
    private static HashSet<Dot> HALF_FILLED = new HashSet<>();
    private static final int VOFFSET = 3;
    private int width = 0;
    private int height = 0;

    private final int MAXWIDTH = 7;
    private final int MAXHEIGHT = 9;
    private final int MINWIDTH = 3;
    private final int MINHEIGHT = 3;

    private GridDot getBoundsCalculated(int data) {
        float lefter = 100, righter = -100, higher = 100, lower = -100;

        if(dots == null){
            //TODO: Dinamizzare
            lefter = 1;
            righter = 7;
            higher = 3;
            lower = 12;
        } else {

            for (Dot dot : dots) {
                if (dot.gridX() < lefter) lefter = dot.gridX();
                if (dot.gridX() > righter) righter = dot.gridX();
                if (dot.gridY() < higher) higher = dot.gridY();
                if (dot.gridY() > lower) lower = dot.gridY();
            }
        }

        if(data == SIZE) {
            return new GridDot(righter - lefter, lower - higher);
        }
        return new GridDot(righter + lefter, lower + higher).half().gridDot();
    }


    public GridDot getCenter() {
        return getBoundsCalculated(CENTER);
    }

    public GridDot getMaxSize() {
        return getBoundsCalculated(SIZE);
    }

    public void incrementSize(int w, int h) {
        w+=width;
        h+=height;
        if(w>MAXWIDTH)  w = MINWIDTH;
        if(h>MAXHEIGHT) h = MINHEIGHT;
        this.fillType = FillType.CUSTOM;
        this.dots = getGridSized(w,h);
    }

    public void incrementWidth(int w) {
        incrementSize(w,0);
    }

    public void incrementHeight(int h) {
        incrementSize(0,h);
    }

    public String XMLString() {
        if(dots == null || dots.isEmpty()) return fillType.name();

        String r = "";
        for (Dot dot : dots) {
            r += "<dot>" + dot.XMLString() + "</dot>\n";
        }
        return r;
    }

    public enum FillType {
        FILLED, CUSTOM, HALF_FILLED
    }

    private FillType fillType = FillType.CUSTOM;
    private HashSet<Dot> dots;
    private static int gridSize = Consts.baseGridSize;

    // Costruttori
    public Grid(int w, int h) {
        this.fillType = FillType.CUSTOM;
        this.dots = getGridSized(w,h);
        initGrids();
        initPaints();
    }

    public Grid(int w, int h, int x, int y) {
        this.fillType = FillType.CUSTOM;
        this.dots = getGridSized(w,h,x,y);
        initGrids();
        initPaints();
    }

    private HashSet<Dot> getGridSized(int w, int h) {
        int x = (Consts.hStep - w ) / 2;
        int y = (Consts.vStep - h ) / 2;
        return getGridSized(w, h, x, y);
    }

    private HashSet<Dot> getGridSized(int w, int h, int x, int y) {
        width = w;
        height = h;
        HashSet<Dot> R = new HashSet<>();
        for (int i = x+1; i <= w+x; i++) {
            for (int j = y+1; j <= h+y; j++) {
                R.add(new GridDot(i,j));
            }
        }

        return R;
    }

    public Grid(Collection<Dot> dots) {
        this.fillType = FillType.CUSTOM;
        this.dots = new HashSet<>(dots);
        initGrids();
        initPaints();
    }

    public Grid(FillType fillType) {
        this.fillType = fillType;
        initGrids();
        initPaints();
    }

    // Funzioni di supporto
    private Dot nearest(Dot from, FillType fillType){

        switch(fillType){
            case FILLED:
                return Utils.nearest(from, FILLED);
            case HALF_FILLED:
                return Utils.nearest(from, HALF_FILLED);
            case CUSTOM:
            default:
                return Utils.nearest(from, dots);
        }

    }

    public Dot nearest(Dot from){

        return nearest(from, fillType);

    }

    public boolean contains(Dot dot){
        switch(fillType){
            case FILLED:
                return dot.pixelX() % gridSize   == 0 && dot.pixelY() % gridSize   == 0;
            case HALF_FILLED:
                return dot.pixelX() % gridSize*2 == 0 && dot.pixelY() % gridSize*2 == 0;
            case CUSTOM:
            default:
                return dots.contains(dot);
        }
    }

    @Override
    protected void initPaints() {
        Paints.put(MAINPAINT, 0xEEFFFFFF);
    }

    // Disegno
    public void render(Canvas canvas) {
        switch(fillType) {
            case FILLED:
                for (int x = gridSize; x <= Consts.W-gridSize; x += gridSize) {
                    for (int y = gridSize*VOFFSET; y <= Consts.H-gridSize; y += gridSize) {
                        canvas.drawCircle(x, y, Consts.dotSize, Paints.get(MAINPAINT, alpha()));
                    }
                }
                break;
            case HALF_FILLED:
                for (int x = gridSize; x <= Consts.W-gridSize; x += gridSize*2) {
                    for (int y = gridSize*VOFFSET; y <= Consts.H-gridSize; y += gridSize*2) {
                        canvas.drawCircle(x, y, Consts.dotSize, Paints.get(MAINPAINT, alpha()));
                    }
                }
                break;
            case CUSTOM:
            default:
                HashSet<Dot> _dots = new HashSet<>(dots);
                for (Dot dot : _dots) {
                    float x = dot.pixelX(), y = dot.pixelY();
                    canvas.drawCircle(x, y, Consts.dotSize, Paints.get(MAINPAINT, alpha()));
                }
                break;
        }

    }


    private static void initGrids(){
        for (int x = gridSize; x <= Consts.W-gridSize; x += gridSize) {
            for (int y = gridSize*VOFFSET; y <= Consts.H-gridSize; y += gridSize) {
                FILLED.add(new PixelDot(x,y));
            }
        }

        for (int x = gridSize; x <= Consts.W-gridSize; x += gridSize*2) {
            for (int y = gridSize*VOFFSET; y <= Consts.H-gridSize; y += gridSize*2) {
                HALF_FILLED.add(new PixelDot(x, y));
            }
        }

    }

    public void addDot(Dot dot){
        dots.add(dot);
    }

    public void removeDot(Dot dot){
        dots.remove(dot);
    }

    public void toggleOnTap(PixelDot dot){
        // First: is there already a dot here?
        Dot nearestInFullGrid = nearest(dot, FillType.FILLED);
        Dot nearestInRealGrid = nearest(dot);

        if (nearestInFullGrid.equals(nearestInRealGrid)) {
            // They are the same, so this were an existent dot (and I wanna REMOVE it)
            removeDot(nearestInRealGrid);
        } else {
            // Ok, they are different, so is clear that the player wants to ADD
            addDot(nearestInFullGrid);
        }


    }

}
