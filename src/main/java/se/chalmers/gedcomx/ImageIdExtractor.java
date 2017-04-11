package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.records.Field;
import org.gedcomx.records.FieldValue;

/**
 * Created by Leif on 2017-04-11.
 */
public class ImageIdExtractor {

    public String extractImageId(Gedcomx g, String separator){
        String image_nbr = "";
        String parish = "";
        String series = "";
        String volume = "";
        for (Field field : g.getFields()){
            for (FieldValue value : field.getValues()) {
                switch(value.getLabelId()) {
                    case "SORT_VALUE":
                        image_nbr = value.getText();
                        break;
                    case "WP_PARISH":
                    case "EVENT_PARISH":
                    case "EVENT_PLACE_LEVEL_3":
                        parish = value.getText();
                        break;
                    case "WP_SERIES":
                    case "SOURCE_SERIES_NAME":
                        series = value.getText();
                        break;
                    case "WP_VOLUME":
                    case "SOURCE_VOLUME":
                        volume = value.getText();
                        break;
                    default:
                        // Do nothing
                }
            }

        }
        if(image_nbr.equals("")) {
            Debug.writeXml(g);
            System.exit(1);
        }
        return image_nbr + separator + parish + separator + series + separator + volume;
    }
}
