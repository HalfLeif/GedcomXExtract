package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.fileformat.DefaultXMLSerialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Leif on 2017-03-09.
 */
public class Debug {
    /**
     * Serializes a Gedcomx object as XML. Useful for debugging.
     * */
    public static void writeXml(Gedcomx g) {
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
