package ru.compscicenter.informational_retrieval;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;

//import org.mozilla.universalchardet.Constants.CharsetListener.UniversalDetector

public class Indexer {

    private LuceneMorphology morphology;
    private CoordinateIndex index;

    public Indexer(CoordinateIndex out) throws IOException {
        index = out;
        try {
            morphology = new RussianLuceneMorphology();
        }
        catch (IOException e) {
            System.out.println("Something wrong with russianmorphology :'(");
            throw e;
        }
    }

    public void indexDirectory(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("invalid directory name");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                Long startTime = System.currentTimeMillis();
                try {
                    indexFile(file);
                }
                catch (IOException e) {
//                    System.out.println(e.getMessage());
                    System.out.println("file " + file.getName() + " was skipped because of I/O Exception");
                }
                Long estimatedTime = System.currentTimeMillis() - startTime;
                System.out.println("file №" + ++count + " " + file.getName() + " was indexed in " + estimatedTime + " ms");
            }
        }
    }

    public void indexFile(File document) throws IOException {
        Map<String, Coordinates> vocabulary = readDocument(document);
        index.addDocument(document.getName(), vocabulary);
    }

    private Map<String, Coordinates> readDocument(File document) throws IOException {
        Map<String, Coordinates> result;
        try (
                RandomAccessFile docRaf = new RandomAccessFile(document, "r");
                FileChannel fileChannel = docRaf.getChannel()
        ) {
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);

            IndexReader indexReader = new IndexReader(charBuffer, morphology);
            result = indexReader.read();
        }
        return result;
    }

    public void printIndex(File output) throws IOException {
        Long startTime = System.currentTimeMillis();
        try (
                FileOutputStream outputStream = new FileOutputStream(output);
                BufferedOutputStream out = new BufferedOutputStream(outputStream);
                ObjectOutputStream objectOut = new ObjectOutputStream(out)
        ) {
            objectOut.writeObject(getIndex());

            Long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("index was printed in " + estimatedTime + " ms");
        }
    }

    public CoordinateIndex getIndex() {
        return index;
    }
}
