package ru.compscicenter.informational_retrieval;

import org.apache.lucene.morphology.LuceneMorphology;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class IndexReader {

    private final CharBuffer buffer;
    private LuceneMorphology morphology;
    private Integer position;
    private char    currentChar;
    private Boolean isActive;
    private HashMap<String, Coordinates> output;

    private static boolean isValidChar(char c) {
        return (
                ((c >= 'а') && (c <= 'я'))
                        || ((c >= 'А') && (c <= 'Я'))
                        || (c == 'ё') || (c == 'Ё')
                        || (c == '-')
                        || ((c >= 'a') && (c <= 'z'))
                        || ((c >= 'A') && (c <= 'Z'))
        );
    }

    public IndexReader(CharBuffer buffer, LuceneMorphology morphology) {
        this.buffer = buffer;
        this.morphology = morphology;
        if (buffer.hasRemaining()) {
            isActive = true;
            currentChar = buffer.get();
            position = 0;
        }
        else {
            isActive = false;
        }
        output = new HashMap<>();
    }

    private void advance() {
        if (isActive && buffer.hasRemaining()) {
            currentChar = buffer.get();
        }
        else {
            isActive = false;
        }
    }

    private void skipInvalid() {
        while (isActive && !isValidChar(currentChar)) {
            advance();
        }
    }

    public Map<String, Coordinates> read() {
        while (isActive) {
            skipInvalid();
            StringBuilder word = new StringBuilder();
            while (isActive && isValidChar(currentChar)) {
                word.append(Character.toLowerCase(currentChar));
                advance();
            }
            for (String normalForm : analyzeWord(word.toString())) {
                addWord(normalForm);
            }
        }
        return output;
    }

    private void addWord(String word) {
        Coordinates coord = output.get(word);
        if (coord == null) {
            coord = new Coordinates(position);
            output.put(word, coord);
        }
        else {
            coord.addPoint(position);
        }
    }

    private HashSet<String> analyzeWord(String word) {
        HashSet<String> result = new HashSet<>();
        word = trim(word);
        if (word.length() == 0) return result;
        if (morphology.checkString(word)) {
            result.addAll(morphology.getNormalForms(word));
        }
        else {
            result.add(word);
        }
        ++position;
        return result;
    }

    public String trim(String word) {
        int length = word.length();
        int first = 0;
        while ((first < length) && (word.charAt(first) == '-')) {
            first++;
        }
        while ((first < length) && (word.charAt(length - 1) == ' ')) {
            --length;
        }
        return word.substring(first, length);
    }
}
