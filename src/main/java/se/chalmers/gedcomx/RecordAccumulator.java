package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Leif on 2017-03-09.
 */
public class RecordAccumulator {
    private final Map<String, Set<String>> imageToYear = new HashMap<>();
    private final ImageLocator imageLocator = new ImageLocator();
    private final YearExtractor yearExtractor = new YearExtractor();

    public void printAll() {
        for (String imageName : imageToYear.keySet()) {
            StringBuilder builder = new StringBuilder();
            builder.append(imageName);

            Set<String> years = imageToYear.get(imageName);
            appendYears(builder, years);

            System.out.println(builder.toString());
        }
    }
    private void appendYears(StringBuilder builder, Set<String> years) {
        builder.append(", [");
        boolean firstIteration = true;
        for (String year : years) {
            if (firstIteration) {
                firstIteration = false;
            } else {
                builder.append(", ");
            }
            builder.append(year);
        }
        builder.append("]");
    }

    public void addItem(Gedcomx g) {
        String imageName = imageLocator.extractImageName(g);
        String year = yearExtractor.extractYear(g);
        Set<String> value = findOrInsert(imageName);
        if (year != null) {
            value.add(year);
        }
    }

    private Set<String> findOrInsert(String key) {
        Set<String> set = imageToYear.get(key);
        if (set == null) {
            set = new TreeSet<>();
            imageToYear.put(key, set);
        }
        return set;
    }
}
