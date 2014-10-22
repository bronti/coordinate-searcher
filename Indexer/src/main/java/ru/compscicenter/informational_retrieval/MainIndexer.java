package ru.compscicenter.informational_retrieval;

import java.io.*;


public class MainIndexer {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("2 arguments are required: INPUT_DIR OUTPUT_FILE");
            System.exit(1);
        }

        Indexer indexer = new Indexer(new CoordinateIndex());

//        indexer.indexDirectory(new File("data/small_utf8_data/"));
        indexer.indexDirectory(new File(args[0]));

//        File outputFile = new File("result/index.txt");
        File outputFile = new File(args[1]);

        indexer.printIndex(outputFile);

    }
}
