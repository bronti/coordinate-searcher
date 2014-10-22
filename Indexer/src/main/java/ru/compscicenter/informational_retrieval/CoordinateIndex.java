package ru.compscicenter.informational_retrieval;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class CoordinateIndex implements Serializable {

    private HashMap<String, HashMap<Integer, Coordinates>> index;
    private ArrayList<String> documents;

    public CoordinateIndex() throws IOException {
        index = new HashMap<>();
        documents = new ArrayList<>();
    }

    public void addDocument (String name, Map<String, Coordinates> vocabulary) {
        int documentId = documents.size();
        documents.add(name);
        if (vocabulary == null) return;
        for (String word : vocabulary.keySet()) {
            HashMap<Integer, Coordinates> tail = index.get(word);
            if (tail == null) {
                tail = new HashMap<>();
                tail.put(documentId, vocabulary.get(word));
                index.put(word, tail);
            }
            else {
                tail.put(documentId, vocabulary.get(word));
            }
        }
    }

    private Set<Integer> getDocumentIdsByWord(String word) {
        HashMap<Integer, Coordinates> tail = index.get(word);
        if (tail == null) return new HashSet<>();
        return tail.keySet();
    }

    private Set<Integer> getDocumentIdsByWords(List<String> words) {
        if (words.size() == 0) return new HashSet<>();
        Set<Integer> result = new HashSet<>(getDocumentIdsByWord(words.get(0)));
        for (int i = 1; i < words.size(); ++i) {
            result.retainAll(getDocumentIdsByWord(words.get(i)));
        }
        return result;
    }

    private SortedSet<Integer> getCoordinates(int document, String word) {
        return index.get(word).get(document).getCoordinates();
    }

    private SortedSet<Integer> getBoundedCoordinates(int document, String word, int minPos, int maxPos) {
        return index.get(word).get(document).getBounded(minPos, maxPos);
    }

    private boolean checkDocFromWord(int document, CoordinateQuery query, int wordIndex, int prevWordPosition) {
        if (wordIndex >= query.size()) return true;
        CoordinateQuery.Distance distance = query.getDistanceTo(wordIndex);
        final int maxNextPos
                = (distance.direction >= 0)
                ? prevWordPosition + distance.distance
                : prevWordPosition;
        final int minNextPos
                = (distance.direction <= 0)
                ? prevWordPosition - distance.distance
                : prevWordPosition;

        SortedSet<Integer> coordinates
                = getBoundedCoordinates(document, query.getWord(wordIndex), minNextPos, maxNextPos);
        for (int pos : coordinates) {
            if (checkDocFromWord(document, query, wordIndex + 1, pos)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDocumentByQuery(int document, CoordinateQuery query) {
        SortedSet<Integer> initialCoordinates = getCoordinates(document, query.getWord(0));
        for (int i : initialCoordinates) {
            if (checkDocFromWord(document, query, 1, i)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getDocumentsByQuery(CoordinateQuery query) {
        Set<Integer> allDocuments = getDocumentIdsByWords(query.getWords());
        HashSet<String> result = new HashSet<>();
        for (Integer id : allDocuments) {
            if (checkDocumentByQuery(id, query)) {
                result.add(documents.get(id));
            }
        }
        return result;
    }
}
