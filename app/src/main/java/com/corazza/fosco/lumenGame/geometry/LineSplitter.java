package com.corazza.fosco.lumenGame.geometry;

import com.corazza.fosco.lumenGame.gameObjects.Grid;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.helpers.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LineSplitter {

    private Set<Line>  splits      = new HashSet<>();
    private Set<Dot> splitPoints = new HashSet<>();
    private Line line1;
    private Line line2;

    public LineSplitter(Line line1, Line line2, Grid grid) {
        this.line1 = line1;
        this.line2 = line2;

        //Calcolo i punti di Split
        Set<Dot> sPoints = new HashSet<>();
        Utils.AidPoints(sPoints, line1.getSplitPoints(line2));
        Utils.AidPoints(sPoints, line2.getSplitPoints(line1));
        for(Dot point : sPoints) {
            if(grid.contains(point)) splitPoints.add(point);
        }

        switch (splitPoints.size()){
            case 0: // Non ci sono SplitPoints. Niente è cambiato.
                splits.add(line1);
                splits.add(line2);
                break;
            case 1: // C'è un incrocio ed è in diagonale.
                Dot intersecate = (Dot) splitPoints.toArray()[0];
                Segment segment1 = (Segment) line1;
                Segment segment2 = (Segment) line2;

                Segment s1 = new Segment(segment1.gamma, intersecate);
                Segment s2 = new Segment(segment1.theta, intersecate);
                Segment s3 = new Segment(segment2.gamma, intersecate);
                Segment s4 = new Segment(segment2.theta, intersecate);

                if(!s1.length().isZero()) splits.add(s1);
                if(!s2.length().isZero()) splits.add(s2);
                if(!s3.length().isZero()) splits.add(s3);
                if(!s4.length().isZero()) splits.add(s4);
                break;
            default:
                splits.add(line1);
                splits.add(line2);
                break;
        }
    }

    public boolean somethingChanged(){
        if(splits.size() > 2) return true;
        if(splits.size() < 2) return false;
        else {
            ArrayList<Line> _splits = new ArrayList<>(splits);
            boolean FirstIsFirst   = _splits.get(0).equals(line1);
            boolean FirstIsSecond  = _splits.get(1).equals(line1);
            boolean SecondIsFirst  = _splits.get(0).equals(line2);
            boolean SecondIsSecond = _splits.get(1).equals(line2);
            return (FirstIsFirst == FirstIsSecond) || (SecondIsFirst == SecondIsSecond);
        }
    }

    public List<Line> getSplits() {
        return new ArrayList<>(splits);
    }

    public boolean substFrom(List<Line> list){
        if(somethingChanged()) {
            if(line1 != null) list.remove(line1);
            if(line2 != null) list.remove(line2);
            Utils.AidLines(list, splits);
            return true;
        }
        return false;
    }


}
