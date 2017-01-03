package com.corazza.fosco.lumenGame.comparators;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import java.util.Comparator;

public class DotCompare implements Comparator<Dot> {
    @Override
    public int compare(Dot lhs,Dot rhs) {
        if (lhs.pixelX() != rhs.pixelX()) return (int) (lhs.pixelX() - rhs.pixelX());
        if (lhs.pixelY() != rhs.pixelY()) return (int) (lhs.pixelY() -rhs.pixelY());
        return 0;
    }
}