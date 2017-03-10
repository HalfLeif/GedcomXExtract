package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;
import org.gedcomx.common.ResourceReference;
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
            case "Burial":
            case "Death":
                return extractPrimaryYear(g);
            case "Other":
            case "Unspecified":
                return null;
            default:
                System.err.println("Unknown event type " + eventType);
                Debug.writeXml(g);
                System.exit(11);
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
    /**
     * Returns the first principal person.
     * Note that for marriage records there are two principal persons.
     * */
    private Person getPrincipalPerson(Gedcomx g) {
        for (Person person : g.getPersons()) {
            if (person.getPrincipal()) {
                return person;
            }
        }
        return null;
    }
    /**
     * Returns true iff the reference refers to this person.
     * Example: person id: "p_14191196568", ref: "#p_14191196568"
     * */
    private boolean isSamePerson(Person person, ResourceReference ref) {
        if (person == null || ref == null) return false;
        return ref.toString().contains(person.getId());
    }
    private String extractMarriageYear(Gedcomx g) {
        Person principalPerson = getPrincipalPerson(g);
        for (Relationship relationship : g.getRelationships()) {
            // Possible relationships:
            // - Couple (between newly-weds)
            // - Couple (between parents)
            // - ParentChild
            if (coupleURI.equals(relationship.getType()) && isSamePerson(principalPerson, relationship.getPerson1())){
                if (relationship.getFacts() == null) {
                    Debug.writeXml(g);
                }
                for (Fact fact : relationship.getFacts()) {
                    Date date = fact.getDate();
                    if (date == null) {
                        continue;
                    }
                    return extractField(date.getFields(), "EVENT_YEAR");
                }
            }
        }
        return null;
    }
    /**
     * Works for case Baptism, Birth and Death.
     * */
    private String extractPrimaryYear(Gedcomx g) {
        // May actually exist more than one person (like parents, spouse)
        // but principal person is the important one.
        Person principalPerson = getPrincipalPerson(g);
        String result = null;
        for (Fact fact : principalPerson.getFacts()) {
            Date date = fact.getDate();
            if (date == null) {
                continue;
            }
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
    private final URI coupleURI = new URI("http://gedcomx.org/Couple");
}
