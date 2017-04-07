package com.corazza.fosco.lumenGame.lists;

import android.util.Pair;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;

import java.util.ArrayList;

/**
 * Created by Simone on 15/02/2017.
 */

public class ListOfSegmentsWithInclusion extends ArrayList<ListOfSegmentsWithInclusion.Element> {

    public static Element CreateElement(Pair<Dot, Dot> obstacle, Pair<Boolean, Boolean> inclusion) {
        return new Element(obstacle, inclusion);
    }

    public static class Element extends Pair<Pair<Dot, Dot>, Pair<Boolean, Boolean>> {
        public Element(Dot first, Dot second, Boolean firstIncl, Boolean secondIncl) {
            super(new Pair<>(first, second), new Pair<>(firstIncl, secondIncl));
        }

        public Element(Pair<Dot,Dot> first, Pair<Boolean, Boolean> second) {
            super(first, second);
        }

    }
}
