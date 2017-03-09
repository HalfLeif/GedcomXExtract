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
            builder.append(", [");
            for (String year : imageToYear.get(imageName)) {
                builder.append(year);
                builder.append(", ");
            }
            builder.append("]");
            System.out.println(builder.toString());
        }
    }

    public void addItem(Gedcomx g) {
        String imageName = imageLocator.extractImageName(g);
        String year = yearExtractor.extractYear(g);
        if (year == null) {
            year = "empty";
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
