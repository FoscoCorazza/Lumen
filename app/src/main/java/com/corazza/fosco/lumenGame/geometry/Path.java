package com.corazza.fosco.lumenGame.geometry;
import android.graphics.Canvas;
import android.util.Pair;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.schemes.DList;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.gameObjects.Grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Path extends SchemeLayoutDrawable {

    private static final int TEXT_FADING = -2;
    private ArrayList<Line> lines;
    private boolean showLength = false;
    private boolean isTextFadingOut = false;
    private float text_opacity = 1;

    public Path(ArrayList<Line> segments) {
        super();
        this.lines = segments;
    }

    public Path(int[] data, int gridSize, Line.DrawingSettings drawingSettings) {
        super();
        ArrayList<Line> lines = new ArrayList<>();
        Segment toAdd;
        for (int i = 3; i < data.length; i+=4) {
            toAdd = new Segment(
                    data[i-3] * gridSize,
                    data[i-2] * gridSize,
                    data[i-1] * gridSize,
                    data[i-0] * gridSize
            );

            toAdd.setDrawingSettings(drawingSettings);
            lines.add(toAdd);
        }
        this.lines = lines;
    }

    public Path() {
        super();
        this.lines = new ArrayList<>();
    }

    @Override
    protected void initPaints() {}

    @Override
    public void render(Canvas canvas, int x, int y){
        List<Line> list = new ArrayList<>(lines);
        for(Line line : list) {
            if(line != null) {
                line.inherit(this);
                line.setTextOpacity(text_opacity);
                line.render(canvas, x, y);
            }
        }
    }

    @Override
    public void render(Canvas canvas){
        render(canvas, 0,0);
    }

    public void remove(Line line) {
        if(lines.contains(line)) lines.remove(line);
    }

    public void remove(Collection<Line> linesToRemove) {
        lines.removeAll(linesToRemove);
    }

    public Line add(Line line) {
        line.setShowLength(showLength);
        if(!lines.contains(line)) lines.add(line);
        return line;
    }

    public ArrayList<Line> next(Dot point, Line fromHere){
        ArrayList<Line> r = new ArrayList<>();
        for(Line line : lines){
            if(line.startsAt(point) && !line.equals(fromHere)) r.add(line);
        }
        return r;
    }

    public List<Line> splitAt(Dot dot) {
        return splitAt(lines, dot);
    }

    public void splitAt(List<Line> splLines, Grid grid) {
        boolean somethingChanged = false;
        LineSplitter lineSplitter = null;

        for(int i = 0; i < lines.size() && !somethingChanged; i++) {
            for (int j = 0; j < splLines.size() && !somethingChanged; j++) {
                splLines.get(j).setShowLength(showLength);
                if(!splLines.get(j).equals(lines.get(i))) {
                    lineSplitter = new LineSplitter(splLines.get(j), lines.get(i), grid);
                    somethingChanged = lineSplitter.somethingChanged();
                }
            }
        }

        if(lineSplitter != null && somethingChanged){
            lineSplitter.substFrom(lines);
            splitAt(lines, grid);
        }
        else {
            removeDuplicates();
        }

    }

    public List<Line> splitAt(Line lineToSplit, Dot whereToSplit) {
        List<Line> splLines = new ArrayList<>();
        splLines.add(lineToSplit);
        return splitAt(splLines, whereToSplit);
    }

    public List<Line> splitAt(List<Line> linesToSplit, Dot whereToSplit) {
        List<Line> R = new ArrayList<>();
        for (int j = 0; j < linesToSplit.size(); j++) {
            Segment s = (Segment) linesToSplit.get(j);
            s.setShowLength(showLength);
            if(s.contains(whereToSplit) && !whereToSplit.equals(s.gamma) && !whereToSplit.equals(s.theta)) {
                R.add(new Segment(s.gamma, whereToSplit));
                R.add(new Segment(s.theta, whereToSplit));
            } else {
                R.add(s);
            }
        }

        remove(linesToSplit);
        Utils.AidLines(lines, R);
        return R;

    }

    public int contains(Line line) {
        int i = 0;
        for(int j = 0; j < lines.size(); j++) {
            if(lines.get(j).equals(line)) i++;
        }
        return i;
    }

    public void reset() {
        ArrayList<Line> fixed = new ArrayList<>();
        for(Line l : lines){
            if(l.isFixed()) fixed.add(l);
        }
        lines.clear();
        lines.addAll(fixed);
    }

    private void removeDuplicates(){
        ArrayList<Line> newLines = new ArrayList<>();
        boolean contains;
        for(Line exLine : lines){
            contains = false;
            for(Line newLine : newLines) if(newLine.equals(exLine)) contains = true;
            if(!contains) newLines.add(exLine);
        }
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void undo() {
        if(lines!= null && lines.size() > 0) lines.remove(lines.size()-1);
    }

    public void setMode(boolean mode) {
        showLength = mode;
        for(Line line : lines){
            line.setShowLength(mode);
        }
    }


    // Qui ci sono tutte le cose Zandroniane.

    //Dijkstra ritorna un punto (x;y) dove
    //x: lunghezza del path minimo
    //y: numero di Path che raggiungono il path minimo
    private HashMap<Dot, Radical> dijkstra(Dot source){
        if(lines != null && lines.size() > 0 ) {
            if(source instanceof GridDot){
                source = source.pixelDot();
            }
            HashSet<Dot> Q = new HashSet<>();
            HashSet<Dot> G = getGraph();
            HashMap<Dot, Radical> dist = new HashMap<>();
            HashMap<Dot, Dot> prev = new HashMap<>();

            for (Dot v : G) {
                dist.put(v, Radical.Infinite);
                prev.put(v, null);
                Q.add(v);
            }

            dist.put(source, Radical.Zero);

            while(!Q.isEmpty()){
                Dot u = minDistanceDot(dist, Q);
                Q.remove(u);

                if(Radical.Infinite.equals(dist.get(u))) break;

                HashMap<Dot, Radical> neighbors = getNeighbors(u);

                for (Map.Entry<Dot, Radical> entry : neighbors.entrySet()) {
                    Dot v = entry.getKey();
                    Radical alt = dist.get(u).sum(entry.getValue());
                    if (alt.LessThen(dist.get(v))) {
                        dist.put(v, alt);
                        prev.put(v, u);
                    }
                }
            }


            return dist;

        }

        return null;
    }

    private Dot minDistanceDot(HashMap<Dot, Radical> dist, HashSet<Dot> q) {
        Dot minDot = null;
        Radical minValue = Radical.Infinite;

        for(Map.Entry<Dot, Radical> entry : dist.entrySet()){
            if(entry.getValue().LessOrEqualTo(minValue) && q.contains(entry.getKey())){
                minDot = entry.getKey();
                minValue = entry.getValue();
            }
        }

        return minDot;
    }

    private HashSet<Dot> getGraph(){
        HashSet<Dot> R = new HashSet<>();
        for(Line s : lines){
            R.add(s.getFirst(Line.Direction.GAMMATHETA));
            R.add(s.getLast(Line.Direction.GAMMATHETA));
        }
        return R;
    }

    private HashMap<Dot, Radical> getNeighbors(Dot u){
        HashMap<Dot, Radical> R = new HashMap<>();
        for(Line l : lines){
            if(l instanceof Segment) {
                Segment s = (Segment) l;
                if (u.equals((s.getFirst(Line.Direction.GAMMATHETA)))) {
                    Dot neighbor = s.getLast(Line.Direction.GAMMATHETA);
                    R.put(neighbor,  s.length());
                }
                if (u.equals((s.getLast(Line.Direction.GAMMATHETA)))) {
                    Dot neighbor = s.getFirst(Line.Direction.GAMMATHETA);
                    R.put(neighbor,  s.length());
                }
            }
        }

        return R;
    }

    public Radical minDistance(Dot source, Dot endpoint){
        HashMap<Dot, Radical> distanceVector = dijkstra(source);
        if(distanceVector != null && distanceVector.entrySet().size() > 0) {
            for (Map.Entry<Dot, Radical> distance : distanceVector.entrySet()) {
                if (endpoint.equals(distance.getKey())) {
                    return distance.getValue();
                }
            }
        }
        return Radical.Infinite;
    }

    public void notifyTextFadeOut() {
        isTextFadingOut = true;
        setTimeElapsed(TEXT_FADING, 0);
    }

    @Override
    protected void updateOpacity(){
        super.updateOpacity();
        if(isTextFadingOut) {
            text_opacity = valueOfNow(TEXT_FADING, 1, 0, 0, FADING_TIME, AnimType.DEFAULT);
        } else {
            text_opacity = 1;
        }

    }

    public DList<Segment> getSegments() {
        DList<Segment> r = new DList<>();
        for (Line l : lines) if(l instanceof Segment) r.add((Segment) l);
        return r;
    }


}
