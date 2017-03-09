package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.fileformat.DefaultXMLSerialization;
import org.gedcomx.util.RecordSetIterator;

import javax.xml.stream.XMLStreamException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Leif on 2017-02-27.
 */
public class Main {

    public static void main(String[] args){
        String filepath = args[0];

        try {
            ImageLocator locator = new ImageLocator();
            YearExtractor yearExtractor = new YearExtractor();

            RecordSetIterator it = new RecordSetIterator(filepath);
            while(it.hasNext()){
                Gedcomx g = it.next();
                String imageName = locator.extractImageName(g);
                String year = yearExtractor.extractYear(g);
                System.out.println(imageName);
                System.out.println(year);
                break;
            }
            it.close();
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes a Gedcomx object as XML. Useful for debugging.
     * */
    private static void writeXml(Gedcomx g) {
        String outfile = "temp.xml";
        try{
            OutputStream outstream = new FileOutputStream(outfile);
            DefaultXMLSerialization x = new DefaultXMLSerialization(true, Gedcomx.class);
            x.serialize(g, outstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
