package ru.compscicenter.informational_retrieval;

import java.util.ArrayList;

public class CoordinateQuery {

    private ArrayList<String> words;
    private ArrayList<Distance> distances;

    public CoordinateQuery(String word) {
        words = new ArrayList<>(1);
        words.add(word);
        distances = new ArrayList<>(1);
        distances.add(new Distance(0, (byte)0));
    }

    public void addWord(String word, Distance fromPrev) {
        words.add(word);
        distances.add(fromPrev);

    }

    public String getWord(Integer i){
        return words.get(i);
    }

    public Distance getDistanceTo(Integer i) {
        return distances.get(i);
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public static class Distance {
        public Distance(int dist, byte direction) {
            this.direction = direction;
            this.distance = dist;
        }

        public Distance (String text) {
            char sign = text.charAt(0);
            if (sign == '+') {
                direction = 1;
            }
            else if (sign == '-') {
                direction = -1;
            }
            else {
                direction = 0;
            }
            distance = Math.abs(Integer.parseInt(text));
        }

        public final int  distance;
        public final byte direction;
    }
}
