package dayone;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Day 1: Historian Hysteria
 */
public class LocationHintFinder {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d1_input.txt");

        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        Map<Integer, Integer> m = new HashMap<>();

        assert inputStream != null;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                int n1 = scanner.nextInt();
                l1.add(n1);

                int n2 = scanner.nextInt();
                l2.add(n2);

                if (m.containsKey(n2)) {
                    m.compute(n2, (k, v) -> v + 1);
                } else {
                    m.put(n2, 1);
                }
            }
        }

        Collections.sort(l1);
        Collections.sort(l2);

        int distance = 0;
        int similarityScore = 0;

        for (int i = 0 ; i < l1.size(); i++) {
            int n1 = l1.get(i);
            int diff = Math.abs(n1 - l2.get(i));
            distance += diff;

            int subScore = m.getOrDefault(n1, 0) * n1;
            similarityScore += subScore;
        }

        System.out.println("Distance: " + distance);
        System.out.println("Similarity Score: " + similarityScore);
    }
}
