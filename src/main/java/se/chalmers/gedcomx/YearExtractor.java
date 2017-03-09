package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.conclusion.Date;
import org.gedcomx.conclusion.Fact;
import org.gedcomx.conclusion.Relationship;
import org.gedcomx.records.Field;
import org.gedcomx.records.FieldValue;

/**
 * Created by Leif on 2017-03-09.
 */
public class YearExtractor {
    public String extractYear(Gedcomx g) {
        String eventType = extractEventType(g);
        switch (eventType) {
            case "Marriage":
                return extractMarriageYear(g);
            default:
                System.err.println("Unknown event type " + eventType);
        }
        return null;
    }
    private String extractEventType(Gedcomx g) {
        for (Field field : g.getFields()) {
            for (FieldValue value : field.getValues()) {
                if ("EVENT_TYPE".equals(value.getLabelId())) {
                    return value.getText();
                }
            }
        }
        return null;
    }
    private String extractMarriageYear(Gedcomx g) {
        for (Relationship relationship : g.getRelationships()) {
            for (Fact fact : relationship.getFacts()) {
                Date date = fact.getDate();
                for (Field field : date.getFields()) {
                    for (FieldValue value : field.getValues()) {
                        if ("EVENT_YEAR".equals(value.getLabelId())) {
                            return value.getText();
                        }
                    }
                }
            }
        }
        return null;
    }
}
