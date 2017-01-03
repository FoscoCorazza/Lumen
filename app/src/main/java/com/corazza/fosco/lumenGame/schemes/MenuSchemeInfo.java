package com.corazza.fosco.lumenGame.schemes;

import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.gameObjects.Level;
import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.MenuSchemeLayout;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 22/08/2016.
 */
public class MenuSchemeInfo  {
    private List<Level> levels;
    private String code;
    private String name;
    private Grid grid;
    private Path path;

    public MenuSchemeInfo(String code, String name, List<Level> levels, Grid grid, Path path) {
        this.code = code;
        this.name = name;
        this.levels = levels;
        this.grid = grid;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public MenuSchemeLayout toMenuSchemeLayout(MenuSchemeLayout layout) {
        layout.setCode(code);
        layout.setName(name);
        layout.setGrid(grid);
        layout.setPath(path != null ? path : new Path(new ArrayList<Line>()));
        layout.setLevels(levels);
        layout.setButtons();
        return layout;
    }
}
