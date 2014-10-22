package ru.compscicenter.informational_retrieval;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("1 argument is required: INDEX_FILE");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
//        File inputFile = new File("result/index.txt");

        Searcher searcher = new Searcher(inputFile);
        SearchMachine machine = new SearchMachine(searcher);
        machine.provideSearch();
    }
}
