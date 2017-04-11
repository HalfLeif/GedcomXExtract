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
//        File outputTestDir = new File(outputDir, "test");
//        File outputTrainDir = new File(outputDir, "train");
//        outputTestDir.mkdirs();
//        outputTrainDir.mkdirs();
        File outputSubDir = new File(outputDir, "page_index");
        outputSubDir.mkdirs();

        for (File collection : recordsDir.listFiles()) {
            if (collection.isDirectory()) {
                try {
                    parseCollection(collection, outputSubDir);
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
    private static void parseCollection(File collectionDir, File outputSubDir)
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

        File indexfile = new File(outputSubDir, collectionName + ".csv");
        writeLabels(collectionLabels, indexfile);
    }

    private static void writeLabels(RecordAccumulator labels, File indexfile)
            throws IOException {
        indexfile.createNewFile();

        PrintWriter testWriter = new PrintWriter(indexfile);
        labels.outputAll(testWriter);
//        labels.outputLabels(trainWriter, testWriter, TEST_RATIO);
        testWriter.close();
        System.out.println("\tWrote records to " + indexfile.getName());
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
