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
     * 1. Directory of the records containing collections like 1647578, "sweden/records/".
     * 2. Name of output directory. Will create a subdirectory for each collection.
     * */
    public static void main(String[] args){
        File recordsDir = new File(args[0]);
        File outDir = new File(args[1]);

        for (File collection : recordsDir.listFiles()) {
            if (collection.isDirectory()) {
                parseGedcomDir(collection, outDir);
            }
        }
    }

    private static void parseGedcomDir(File collectionDir, File outputDir) {
        String collectionName = collectionDir.getName();
        System.out.println("Collection "+ collectionName);
        File outputCollectionDir = new File(outputDir, collectionName);
        outputCollectionDir.mkdirs();

        File recordsDir = new File(collectionDir, "RecordChunk");
        for (File gzFile : recordsDir.listFiles()) {
            if ("gz".equals(FilenameUtils.getExtension(gzFile.getName()))){
                String newName = FilenameUtils.getBaseName(gzFile.getName());
                File outfile = new File(outputCollectionDir, newName + ".csv");
                try {
                    outfile.createNewFile();
                    PrintWriter writer = new PrintWriter(outfile);
                    parseGedcomFile(gzFile, writer);
                    writer.close();

                    System.out.println("\tWrote " + gzFile.getName() + " to " + outfile.getName());
                } catch (XMLStreamException | IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * Parses this gedcomx .gz file into this Appendable.
     * */
    private static void parseGedcomFile(File gzFile, Appendable output) throws IOException, XMLStreamException {
        RecordAccumulator accumulator = new RecordAccumulator();
        FileInputStream instream = new FileInputStream(gzFile);
        RecordSetIterator it = new RecordSetIterator(instream, true);
        while(it.hasNext()){
            Gedcomx g = it.next();
            accumulator.addItem(g);
        }
        it.close();
        accumulator.printAll(output);
    }
}
