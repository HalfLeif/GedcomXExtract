package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.common.URI;
import org.gedcomx.conclusion.Date;
import org.gedcomx.conclusion.Fact;
import org.gedcomx.conclusion.Person;
import org.gedcomx.conclusion.Relationship;
import org.gedcomx.records.Field;
import org.gedcomx.records.FieldValue;

import java.util.List;

/**
 * Created by Leif on 2017-03-09.
 */
public class YearExtractor {
    public String extractYear(Gedcomx g) {
        String eventType = extractEventType(g);
        switch (eventType) {
            case "Marriage":
                return extractMarriageYear(g);
            case "Baptism":
            case "Birth":
            case "Death":
                return extractPrimaryYear(g);
            case "Other":
                return null;
            default:
                System.err.println("Unknown event type " + eventType);
        }
        return null;
    }
    private String extractField(List<Field> fields, String label_id) {
        for (Field field : fields) {
            for (FieldValue value : field.getValues()) {
                if (label_id.equals(value.getLabelId())) {
                    return value.getText();
                }
            }
        }
        return null;
    }
    private String extractEventType(Gedcomx g) {
        return extractField(g.getFields(), "EVENT_TYPE");
    }
    private String extractMarriageYear(Gedcomx g) {
        for (Relationship relationship : g.getRelationships()) {
            for (Fact fact : relationship.getFacts()) {
                Date date = fact.getDate();
                return extractField(date.getFields(), "EVENT_YEAR");
            }
        }
        return null;
    }
    /**
     * Works for case Baptism, Birth and Death.
     * */
    private String extractPrimaryYear(Gedcomx g) {
        // May actually exist more than one person (like parents) but first person should be the principal person.
        Person firstPerson = g.getPerson();
        if (!firstPerson.getPrincipal()) {
            System.err.println("Expected first person to be principal!");
            return null;
        }
        String result = null;
        for (Fact fact : firstPerson.getFacts()) {
            Date date = fact.getDate();
            String event_year = extractField(date.getFields(), "EVENT_YEAR");
            if (event_year != null && fact.getPrimary()) {
                return event_year;
            } else if (birthURI.equals(fact.getType())) {
                result = extractField(date.getFields(), "PR_BIRTH_YEAR");
            }
            // else: return year for birth/burial instead.
        }
        return result;
    }

//    private final URI baptismUri = new URI("http://gedcomx.org/Baptism");
    private final URI birthURI = new URI("http://gedcomx.org/Birth");
}
