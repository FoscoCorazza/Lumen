package com.corazza.fosco.lumenGame.schemes;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.util.Xml;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.gameObjects.Level;
import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Path;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.lists.ListOfSegments;
import com.corazza.fosco.lumenGame.lists.ListOfSegmentsWithInclusion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by Simone on 10/05/2016.
 */
public class SchemeReader {

    private static final String ns = null;
    private static SchemeReader sr;

    public TreeMap<String, SchemeInfo> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readSchemeList(parser);
        } finally {
            in.close();
        }
    }

    private TreeMap<String, MenuSchemeInfo> parseMenu(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readMenuSchemeList(parser);
        } finally {
            in.close();
        }
    }

    private TreeMap<String, SchemeInfo> readSchemeList(XmlPullParser parser) throws XmlPullParserException, IOException {
        TreeMap<String, SchemeInfo> entries = new TreeMap<>();

        parser.require(XmlPullParser.START_TAG, ns, "schemes");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("scheme")) {
                SchemeInfo info = readScheme(parser);
                entries.put(info.getCode(), info);
            } else {
                skip(parser);
            }
        }
        return entries;

    }

    private TreeMap<String, MenuSchemeInfo> readMenuSchemeList(XmlPullParser parser) throws XmlPullParserException, IOException {
        TreeMap<String, MenuSchemeInfo> entries = new TreeMap<>();

        parser.require(XmlPullParser.START_TAG, ns, "schemes");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("scheme")) {
                MenuSchemeInfo info = readMenuScheme(parser);
                entries.put(info.getCode(), info);
            } else {
                skip(parser);
            }
        }
        return entries;

    }

    private SchemeInfo readScheme(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "scheme");
        String code = getAttributeNamed(parser, "code");
        String name = getAttributeNamed(parser, "name");
        String sector = getAttributeNamed(parser, "sector");
        Dot lumen = null;
        List<Pair<Dot, Integer>> bulbs = new ArrayList<>();
        ListOfSegments fixedLines = new ListOfSegments();
        ListOfSegments destructors = new ListOfSegments();
        ListOfSegments deflectors = new ListOfSegments();
        List<Dot> stars = new ArrayList<>();
        Grid grid = null;
        Path path = null;
        //List<Level> levels = new ArrayList<>();

        // COOOOL, List of Pairs composed by two pairs!
        ListOfSegmentsWithInclusion obstructors = new ListOfSegmentsWithInclusion();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            switch (tagname) {
                case "bulb":
                    bulbs.add(readBulb(parser));
                    break;
                case "lumen":
                    lumen = readLumen(parser);
                    break;
                case "line":
                    fixedLines.add(readLines(parser)) ;
                    break;
                case "obst":
                    obstructors.add(readObstructors(parser)) ;
                    break;
                case "dest":
                    destructors.add(readDestructors(parser)) ;
                    break;
                case "defl":
                    deflectors.add(readDeflectors(parser)) ;
                    break;
                case "star":
                    stars.add(readStar(parser)) ;
                    break;
                case "grid":
                    grid = readGrid(parser);
                    break;
                case "path":
                    path = readPath(parser);
                    break;
                case "level":
                    //levels.add(readLevel(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }


        return new SchemeInfo(code, name, sector, lumen, bulbs, fixedLines,
                obstructors, destructors, deflectors, stars, grid, path);
    }


    private MenuSchemeInfo readMenuScheme(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "scheme");
        String code = getAttributeNamed(parser, "code");
        String name = getAttributeNamed(parser, "name");
        Grid grid = null;
        Path path = null;
        List<Level> levels = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            switch (tagname) {
                case "grid":
                    grid = readGrid(parser);
                    break;
                case "path":
                    path = readPath(parser);
                    break;
                case "level":
                    levels.add(readLevel(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }


        return new MenuSchemeInfo(code, name, levels, grid, path);
    }

    private Level readLevel(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "level");
        boolean unlocked = false;
        try{
            unlocked = Boolean.parseBoolean(getAttributeNamed(parser, "unlocked"));
        }catch (Exception ignored){}

        Level level = new Level(getAttributeNamed(parser, "id"), !unlocked);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            switch (tagname) {
                case "dot":
                    parser.require(XmlPullParser.START_TAG, ns, "dot");
                    level.setPosition(readDot(parser));
                    parser.require(XmlPullParser.END_TAG, ns, "dot");
                    break;
                case "unlocks":
                    readUnlocks(parser, level);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "level");
        return level;
    }

    private void readUnlocks(XmlPullParser parser, Level level) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "unlocks");

        String id;
        Path path;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            switch (tagname) {
                case "id":
                    parser.require(XmlPullParser.START_TAG, ns, "id");
                    id = readText(parser);
                    //level.addUnlockedLevels(id.split(";"));
                    parser.require(XmlPullParser.END_TAG, ns, "id");
                    break;
                case "path":
                    path = readPath(parser);
                    //level.addUnlockedPath(path);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, "unlocks");
    }

    private String getAttributeNamed(XmlPullParser parser, String name) {
        int max = parser.getAttributeCount();
        if(max > 0){
            for (int i = 0; i < max; i++) {
                if(name.equals(parser.getAttributeName(i))){
                    return parser.getAttributeValue(i);
                }
            }
        }
        return null;
    }

    private Pair<Dot, Integer> readBulb(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "bulb");
        String n = getAttributeNamed(parser, "need");
        int need = n != null && !n.isEmpty()? Integer.parseInt(n) : 1;
        Dot dot = readDot(parser);
        parser.require(XmlPullParser.END_TAG, ns, "bulb");
        return new Pair<>(dot, need);
    }

    private Dot readLumen(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lumen");
        Dot n = readDot(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lumen");
        return n;
    }

    private Pair<Dot, Dot> readObstacle(XmlPullParser parser, String tagname) throws IOException, XmlPullParserException {
        return readObstacleWithGammaThetaInclusion(parser, tagname).first;
    }

    private ListOfSegmentsWithInclusion.Element readObstacleWithGammaThetaInclusion(XmlPullParser parser, String tagname) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagname);

            String thetAttr = getAttributeNamed(parser, "theta");
            String gammAttr = getAttributeNamed(parser, "gamma");
            boolean thetaIn = thetAttr == null || thetAttr.equals("true");
            boolean gammaIn = gammAttr == null || gammAttr.equals("true");
            Pair<Boolean, Boolean> inclusion = new Pair<>(thetaIn, gammaIn);

        Pair<Dot, Dot> obstacle = readPair(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagname);
        return ListOfSegmentsWithInclusion.CreateElement(obstacle, inclusion);
    }

    private Pair<Dot, Dot> readLines(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readObstacle(parser, "line");
    }

    private ListOfSegmentsWithInclusion.Element readObstructors(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readObstacleWithGammaThetaInclusion(parser, "obst");
    }

    private Pair<Dot, Dot> readDestructors(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readObstacle(parser, "dest");
    }

    private Pair<Dot, Dot> readDeflectors(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readObstacle(parser, "defl");
    }

    private int readNeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "need");
        int n = readInt(parser);
        parser.require(XmlPullParser.END_TAG, ns, "need");
        return n;
    }

    private Dot readStar(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "star");
        Dot n = readDot(parser);
        parser.require(XmlPullParser.END_TAG, ns, "star");
        return n;
    }

    private Grid readGrid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "grid");
        List<Dot> dots =  new ArrayList<>();
        parser.next();
        String str = clean(parser.getText());
        if (str == null || str.isEmpty()) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if(name.equals("dot")){
                    dots.add(readDot(parser));
                }
            }

        } else {
            parser.nextTag();
        }


        parser.require(XmlPullParser.END_TAG, ns, "grid");

        switch (str){
            case "FILLED":
                return new Grid(Grid.FillType.FILLED);
            case "HALF_FILLED":
                return new Grid(Grid.FillType.HALF_FILLED);
            default:
                if(str.startsWith("SIZE")){
                    /* Qui può essere formato così:
                        1. SIZE_N
                        2. SIZE_W_H
                        3. SIZE_W_H_X_Y
                    */

                    String[] splits = str.split("_");
                    int w=0, h=0;
                    int l = splits.length;

                    if ( l == 2 || l == 3) {
                        w = Integer.parseInt(splits[1]);
                        if (l == 2) h = w;
                        if (l == 3) h = Integer.parseInt(splits[2]);
                    } else if (l == 5){
                        w = Integer.parseInt(splits[1]);
                        h = Integer.parseInt(splits[2]);
                        int x = Integer.parseInt(splits[3]);
                        int y = Integer.parseInt(splits[4]);
                        return new Grid(w,h,x,y);
                    }

                    return new Grid(w,h);
                } else {
                    return new Grid(dots);
                }


        }


    }

    private Path readPath(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "path");
        ArrayList<Integer> coordinates =  new ArrayList<Integer>();
        parser.next();
        String str = clean(parser.getText());
        if (str == null || str.isEmpty()) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if(name.equals("line")){
                    // Se ho una line, prendo i quattro numeri e li aggiungo tutti.

                    String strLine = readText(parser);
                    strLine = clean(strLine);
                    String[] line = strLine.split(";");
                    if(line.length == 4) {
                        for (int i = 0; i < 4; i++) {
                            coordinates.add(Integer.parseInt(line[i]));
                        }
                    }
                }
            }

        } else {
            parser.nextTag();
        }


        parser.require(XmlPullParser.END_TAG, ns, "path");

        if(!coordinates.isEmpty()){
            Line.DrawingSettings drawingSettings = Line.getNewDrawingSettings(false, false, Color.WHITE);


            return new Path(toIntArray(coordinates), Consts.baseGridSize, drawingSettings);
        }
        return null;

    }

    private int[] toIntArray(List<Integer> list) {
        int[] data = new int[list.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = list.get(i);
        }
        return data;
    }

    private String clean(String str) {
        if(str == null) str = "";
        str = str.replace("\n", "");
        str = str.replace(" ", "");
        return str;
    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
        String str = readText(parser);
        return Integer.parseInt(str);
    }

    private Dot readDot(XmlPullParser parser) throws IOException, XmlPullParserException {
        String str   = readText(parser);
        str = clean(str);
        String[] dot = str.split(";");
        if(dot.length == 2) {
            return new GridDot(Integer.parseInt(dot[0]), Integer.parseInt(dot[1]));
        }
        return null;
    }

    private Pair<Dot, Dot> readPair(XmlPullParser parser) throws IOException, XmlPullParserException {
        String str   = readText(parser);
        str = clean(str);
        String[] dot = str.split(";");
        if(dot.length == 4) {
            Dot first  =  new GridDot(Integer.parseInt(dot[0]), Integer.parseInt(dot[1]));
            Dot second =  new GridDot(Integer.parseInt(dot[2]), Integer.parseInt(dot[3]));
            return new Pair<>(first, second);
        }
        if(dot.length == 2) {
            Dot first  =  new GridDot(Integer.parseInt(dot[0]), Integer.parseInt(dot[1]));
            return new Pair<>(first, first);
        }
        return null;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



    public static TreeMap<String, SchemeInfo> read(Context context){
        if(sr == null) {
            sr = new SchemeReader();
        }
        InputStream is = context.getResources().openRawResource(R.raw.scheme_list);

        try {
            return sr.parse(is);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return new TreeMap<>();
        }

    }

    public static TreeMap<String, MenuSchemeInfo> readMenu(Context context){
        if(sr == null) {
            sr = new SchemeReader();
        }
        InputStream is = context.getResources().openRawResource(R.raw.menu_scheme_list);

        try {
            return sr.parseMenu(is);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return new TreeMap<>();
        }

    }
}
