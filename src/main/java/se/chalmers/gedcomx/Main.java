package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.util.RecordSetIterator;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Created by Leif on 2017-02-27.
 */
public class Main {

    public static void main(String[] args){
        String filepath = args[0];

        try {
            RecordAccumulator accumulator = new RecordAccumulator();

            RecordSetIterator it = new RecordSetIterator(filepath);
            int iter = 0;
            while(it.hasNext() //&& iter < 10000
                    ){
                ++iter;
                Gedcomx g = it.next();
                accumulator.addItem(g);
            }
            it.close();

            accumulator.printAll();
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }
}
