package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.records.Field;
import org.gedcomx.records.FieldValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Leif on 2017-03-09.
 */
public class ImageLocator {
    public String extractImageName(Gedcomx g){
        for (Field field : g.getFields()){
            for (FieldValue value : field.getValues()){
                if ("IMAGE_ARK".equals(value.getLabelId())) {
                    return arkToName(value.getText());
                }
            }
        }
        return null;
    }

    private String arkToName(String uri){
        Matcher matcher = arkName.matcher(uri);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    private final Pattern arkName = Pattern.compile(".*:([^:]+)$");
}
