package se.chalmers.gedcomx;

import org.apache.commons.io.FilenameUtils;
import org.gedcomx.Gedcomx;
import org.gedcomx.util.RecordSetIterator;

import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * Created by Leif on 2017-02-27.
 */
public class Main {

    private static final double TEST_RATIO = 0.10;

    /**
     * Should pass exactly two arguments to main:
     * 1. Directory of the records containing collections like 1647578, e.g. "sweden/records/".
     * 2. Name of output directory. Will create a subdirectory for each collection.
     *
     * Returns on error.
     * */
    public static void main(String[] args){
        File recordsDir = new File(args[0]);
        File outputDir = new File(args[1]);
        File outputTestDir = new File(outputDir, "test");
        File outputTrainDir = new File(outputDir, "train");
        outputTestDir.mkdirs();
        outputTrainDir.mkdirs();

        for (File collection : recordsDir.listFiles()) {
            if (collection.isDirectory()) {
                try {
                    parseCollection(collection, outputTestDir, outputTrainDir);
                } catch (IOException | XMLStreamException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * Aggregates all relevant labels of this collection into a single file
     * which is created in `outputDir`.
     * */
    private static void parseCollection(File collectionDir, File outputTestDir, File outputTrainDir)
            throws IOException, XMLStreamException {
        String collectionName = collectionDir.getName();
        System.out.println("Collection "+ collectionName);

        RecordAccumulator collectionLabels = new RecordAccumulator();
        File recordsDir = new File(collectionDir, "RecordChunk");
        for (File gzFile : recordsDir.listFiles()) {
            if ("gz".equals(FilenameUtils.getExtension(gzFile.getName()))){
                    parseGedcomFile(gzFile, collectionLabels);
                    System.out.println("\tParsed " + gzFile.getName());
            }
        }

        File testfile = new File(outputTestDir, collectionName + ".csv");
        File trainfile = new File(outputTrainDir, collectionName + ".csv");
        writeLabels(collectionLabels, testfile, trainfile);
    }

    private static void writeLabels(RecordAccumulator labels, File testfile, File trainfile)
            throws IOException {
        testfile.createNewFile();
        trainfile.createNewFile();

        PrintWriter testWriter = new PrintWriter(testfile);
        PrintWriter trainWriter = new PrintWriter(trainfile);
        labels.outputLabels(trainWriter, testWriter, TEST_RATIO);
        testWriter.close();
        trainWriter.close();
        System.out.println("\tWrote records to " + testfile.getName());
    }

    /**
     * Adds this gedcomx .gz file into this accumulator.
     * */
    private static void parseGedcomFile(File gzFile, RecordAccumulator output)
            throws XMLStreamException, IOException {
        FileInputStream instream = new FileInputStream(gzFile);
        RecordSetIterator it = new RecordSetIterator(instream, true);
        while(it.hasNext()){
            Gedcomx g = it.next();
            output.addItem(g);
        }
        it.close();
    }
}
