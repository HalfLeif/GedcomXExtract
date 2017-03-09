package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.conclusion.Person;
import org.gedcomx.fileformat.DefaultXMLSerialization;
import org.gedcomx.util.RecordSetIterator;

import javax.xml.stream.XMLStreamException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leif on 2017-02-27.
 */
public class Main {
    public static void testNull(Object o, String str){
        if (o != null) {
            System.out.println('\t'+str);
            System.out.println('\t'+o.toString());
        }
    }

    public static void visitGedcomx(Gedcomx g){
        System.out.println("Next: " + g.toString());

        testNull(g.getAgents(), "Agents");

        testNull(g.getAttribution(), "Attribution");
        testNull(g.getCoupleRelationships(), "Relationships");
        testNull(g.getDocuments(), "Documents");
        testNull(g.getEvents(), "Events");
        testNull(g.getFamilies(), "Families");
        testNull(g.getFields(), "Fields");
        testNull(g.getPersons(), "Persons");
        for (Person p : g.getPersons()) {
            testNull(p.getFacts(), "\tP-Facts");
            testNull(p.getFields(), "\tP-Fields");
            testNull(p.getGender(), "\tP-Gender");
            testNull(p.getNames(), "\tP-Names");
        }
        testNull(g.getPlaces(), "Places");
        testNull(g.getRecordDescriptors(), "Descriptors");
    }

    public static void main(String[] args){
        String filepath = "resources\\1647598-sample.gz";

        try {
            String outfile = "resources\\out.xml";
            OutputStream outstream = new FileOutputStream(outfile);
            DefaultXMLSerialization x = new DefaultXMLSerialization(true, Gedcomx.class);

            RecordSetIterator it = new RecordSetIterator(filepath);
            while(it.hasNext()){
                Gedcomx g = it.next();
//                visitGedcomx(g);
                x.serialize(g, outstream);
//                break;
            }







            it.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
