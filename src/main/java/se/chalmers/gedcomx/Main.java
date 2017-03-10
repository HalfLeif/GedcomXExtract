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

    /**
     * Should pass exactly two arguments to main:
     * 1. Directory of the records containing collections like 1647578, e.g. "sweden/records/".
     * 2. Name of output directory. Will create a subdirectory for each collection.
     *
     * Returns on error.
     * */
    public static void main(String[] args){
        File recordsDir = new File(args[0]);
        File outDir = new File(args[1]);
        outDir.mkdirs();

        for (File collection : recordsDir.listFiles()) {
            if (collection.isDirectory()) {
                try {
                    parseCollection(collection, outDir);
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
    private static void parseCollection(File collectionDir, File outputDir)
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

        File outfile = new File(outputDir, collectionName + ".csv");
        writeLabels(collectionLabels, outfile);
    }

    private static void writeLabels(RecordAccumulator labels, File outfile)
            throws IOException {
        outfile.createNewFile();
        PrintWriter writer = new PrintWriter(outfile);
        labels.printAll(writer);
        writer.close();
        System.out.println("\tWrote records to " + outfile.getName());
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
