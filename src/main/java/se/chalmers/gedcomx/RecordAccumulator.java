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
    private final String noValue = "empty";

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
            if (years.size() > 0 && noValue.equals(year)) {
                // This set has non-empty values, use them instead.
                continue;
            }

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
        if (year == null) {
            year = noValue;
        }

        findOrInsert(imageName).add(year);
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
