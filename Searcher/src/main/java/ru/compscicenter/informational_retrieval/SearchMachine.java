package ru.compscicenter.informational_retrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class SearchMachine {

    private final Searcher searcher;

    public SearchMachine(Searcher searcher) {
        this.searcher = searcher;
    }

    public void provideSearch() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = reader.readLine().trim()).length() > 0) {
                try {
                    printResult(searcher.find(input));
                }
                catch (Searcher.IllegalQueryException e) {
                    System.out.println("invalid query");
                }
            }
        } catch (IOException e) {
            // do nothing
        }
    }

    private void printResult(Set<String> result) {
        if (result.size() == 0) {
            System.out.println("no documents found");
        }
        else {
            System.out.print("found:");
            int i = 0;
            for (String doc : result) {
                if (i < 3) {
                    System.out.print(" " + doc);
                    ++i;
                }
                else {
                    System.out.print(" and " + (result.size() - i) + " more");
                    break;
                }
            }
            System.out.println();
        }
    }

}
