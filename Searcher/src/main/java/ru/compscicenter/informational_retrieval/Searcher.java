package ru.compscicenter.informational_retrieval;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Set;

public class Searcher {

    public Searcher(File indexFile) {
        Long startTime = System.currentTimeMillis();
        try (
            FileInputStream inputStream = new FileInputStream(indexFile);
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            ObjectInputStream objectIn = new ObjectInputStream(bufferedIn)
        ) {
            index = (CoordinateIndex)objectIn.readObject();

            Long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("index loaded in " + estimatedTime + "ms");
        }
        catch (Exception e) {
            System.out.println("cannot read index");
            System.out.println(e.toString());
        }
    }

    public CoordinateIndex getIndex() {
        return index;
    }

    private CoordinateQuery parseQuery(Lexer lexer) throws IllegalQueryException {
        lexer.checkedAdvance(Lexer.TokenType.WORD);
        CoordinateQuery result = new CoordinateQuery(lexer.getToken().text);
        lexer.advance();
        while (lexer.hasToken() && lexer.getToken().type == Lexer.TokenType.DISTANCE) {
            CoordinateQuery.Distance dist = new CoordinateQuery.Distance(lexer.getToken().text);
            lexer.checkedAdvance(Lexer.TokenType.WORD);
            result.addWord(lexer.getToken().text, dist);
            lexer.advance();
        }
        return result;
    }

    public Set<String> find(String request) throws IllegalQueryException {
        Lexer lexer = new Lexer(request);
        CoordinateQuery query = parseQuery(lexer);
        Set<String> result = index.getDocumentsByQuery(query);
        while (lexer.hasToken()) {
            Lexer.Token token = lexer.getToken();
            query = parseQuery(lexer);
            if (token.type == Lexer.TokenType.AND) {
                result.retainAll(index.getDocumentsByQuery(query));
            }
            else if (token.type == Lexer.TokenType.OR) {
                result.addAll(index.getDocumentsByQuery(query));
            }
            else throw new IllegalQueryException();
        }
        return result;
    }

    private CoordinateIndex index;

    public static class IllegalQueryException extends Exception {
    }
}
