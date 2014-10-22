package ru.compscicenter.informational_retrieval;

import java.io.Serializable;
import java.util.*;

public class Coordinates implements Serializable {

    private TreeSet<Integer> coordinates;

    public Coordinates(Integer point) {
        this.coordinates = new TreeSet<>();
        this.coordinates.add(point);
    }

    public void addPoint(Integer point) {
        coordinates.add(point);
    }

    public SortedSet<Integer> getCoordinates() {
        return new TreeSet<>(coordinates);
    }

    public SortedSet<Integer> getBounded(int minPos, int maxPos) {
        return new TreeSet<>(coordinates.subSet(minPos, maxPos + 1));
    }
}
