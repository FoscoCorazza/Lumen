package com.corazza.fosco.lumenGame.schemes;

import android.util.Pair;

import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.lists.ListOfSegments;
import com.corazza.fosco.lumenGame.lists.ListOfSegmentsWithInclusion;
import com.corazza.fosco.lumenGame.savemanager.SchemeResult;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 10/05/2016.
 */
public class SchemeInfo {

    private String code;
    private String name;
    private String sector;
    private Dot lumen;
    private Grid grid;
    private Path path;
    private SchemeResult result;
    private ListOfSegments fixedLines;
    private ListOfSegmentsWithInclusion obstructors;
    private ListOfSegments destructors;
    private ListOfSegments deflectors;
    private List<Pair<Dot, Integer>> bulbs;
    private List<Dot> stars;

    public SchemeInfo(String code, String name, String sector, Dot lumen, List<Pair<Dot, Integer>> bulbs, ListOfSegments fixedLines,
                      ListOfSegmentsWithInclusion obstructors, ListOfSegments destructors, ListOfSegments deflectors,
                      List<Dot> stars, Grid grid, Path path) {

        this.code = code;
        this.name = name;
        this.sector = sector;
        this.lumen = lumen;
        this.bulbs = bulbs;
        this.grid = grid;
        this.path = path;

        this.fixedLines = fixedLines;
        this.obstructors = obstructors;
        this.destructors = destructors;
        this.deflectors = deflectors;
        this.stars = stars;
    }

    public Dot getLumen() {
        return lumen;
    }

    public void setLumen(Dot lumen) {
        this.lumen = lumen;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Path getPath() {
        path = new Path(new ArrayList<Line>());
        for (Pair<Dot,Dot> pair : fixedLines) {
            Segment s = new Segment(pair.first, pair.second);
            s.setFixed(true);
            path.add(s);
        }
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public SchemeLayout toSchemeLayout(SchemeLayout layout){
        layout.putObstructors(obstructors);
        layout.putDestructors(destructors);
        layout.putDeflectors(deflectors);
        layout.addStars(stars);
        layout.setCode(code);
        layout.setName(name);
        layout.setMainLumen(lumen);
        layout.setBulbs(bulbs);
        layout.setGrid(grid);
        layout.setPath(getPath());
        layout.setToast();
        layout.setMode(true);
        return layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setResult(SchemeResult result) {
        this.result = result;
    }

    public SchemeResult getResult() {
        return result;
    }

    public int getStarsTotal() {
        return stars != null ? stars.size() : 0;
    }


}
