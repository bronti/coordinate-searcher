package ru.compscicenter.informational_retrieval;

public class Lexer {

    private final String text;
    private int          nextI;
    private Token        currentToken;

    public Lexer(String input) {
        text = input;
        nextI = 0;
    }

    private boolean endOfText() {
        return nextI >= text.length();
    }

    private static boolean isValid(char c) {
        return (((c >= 'а') && (c <= 'я'))
                || ((c >= 'А') && (c <= 'Я'))
                || (c == 'ё') || (c == 'Ё')
                || (c == '-')
                || ((c >= 'a') && (c <= 'z'))
                || ((c >= 'A') && (c <= 'Z'))
        );
    }

    private boolean nextMatches(String str) {
        int lastI = nextI + str.length();
        return (lastI <= text.length() && str.equalsIgnoreCase(text.substring(nextI, lastI)));
    }

    private void skipWhitespaces() {
        while (!endOfText() && (Character.isWhitespace(text.charAt(nextI)))) {
            ++nextI;
        }
    }

    public Token getToken () {
        return currentToken;
    }

    public void advance() throws Searcher.IllegalQueryException {
        skipWhitespaces();
        if (endOfText()) {
            currentToken = null;
            return;
        }
        if (nextMatches("and")) {
            nextI += 3;
            currentToken = new Token(TokenType.AND);
        }
        else if (nextMatches("or")) {
            nextI += 2;
            currentToken = new Token(TokenType.OR);
        }
        else if (nextMatches("/")) {
            ++nextI;
            int startI = nextI;
            if (nextMatches("+") || nextMatches("-")) {
                ++nextI;
            }
            while (!endOfText() && Character.isDigit(text.charAt(nextI))) {
                ++nextI;
            }
            if (startI == nextI) throw new Searcher.IllegalQueryException();
            currentToken = new Token(TokenType.DISTANCE, text.substring(startI, nextI));
        }
        else {
            int startI = nextI;
            while (!endOfText() && isValid(text.charAt(nextI))) {
                ++nextI;
            }
            if ((nextI == startI) && !endOfText()) {
                throw new Searcher.IllegalQueryException();
            }
            currentToken = new Token(TokenType.WORD, text.substring(startI, nextI));
        }
    }

    public void checkedAdvance(TokenType requiredType) throws Searcher.IllegalQueryException {
        advance();
        if (currentToken.type != requiredType) throw new Searcher.IllegalQueryException();
    }

    public boolean hasToken() {
        return currentToken != null;
    }

    public enum TokenType {
        AND,
        OR,
        DISTANCE,
        WORD
    }

    public static class Token {
        public Token(TokenType type) {
            this.type = type;
            this.text = "";
        }

        public Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }

        public final TokenType type;
        public final String    text;
    }
}
