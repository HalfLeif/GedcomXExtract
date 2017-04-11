package se.chalmers.gedcomx;

import org.gedcomx.Gedcomx;

import java.io.IOException;
import java.util.*;

/**
 * Created by Leif on 2017-03-09.
 */
public class RecordAccumulator {
    private final Map<String, Set<String>> imageToYear = new HashMap<>();
    private final Map<String, String> imageToId = new HashMap<>();

    private final ImageIdExtractor idExtractor = new ImageIdExtractor();
    private final ImageLocator imageLocator = new ImageLocator();
    private final YearExtractor yearExtractor = new YearExtractor();
    private final Random random = new Random(0);
    private final String fieldSeparator = " | ";

    /**
     * Prints content to this Appendable (e.g. System.out, StringBuilder or PrintWriter).
     * */
    public void outputAll(Appendable dataSet) throws IOException {
        outputLabels(dataSet, null, 0.0);
    }

    /**
     * Divides the labels into a training set and a test set.
     * `testRatio` denotes how much of the data will go into the test set.
     * */
    public void outputLabels(Appendable trainSet, Appendable testSet, double testRatio) throws IOException {
        for (String imageName : imageToYear.keySet()) {
            Appendable dataSet = trainSet;
            if (random.nextDouble() < testRatio) {
                dataSet = testSet;
            }
            dataSet.append(imageName);
            dataSet.append(fieldSeparator);

            Set<String> years = imageToYear.get(imageName);
            appendYears(dataSet, years);
            dataSet.append(fieldSeparator);

            dataSet.append(imageToId.get(imageName));

            dataSet.append('\n');
        }
    }

    private void appendYears(Appendable builder, Set<String> years) throws IOException {
        builder.append("[");
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

        imageToId.put(imageName, idExtractor.extractImageId(g, fieldSeparator));
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
