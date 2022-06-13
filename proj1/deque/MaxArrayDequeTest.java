package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;


public class MaxArrayDequeTest {

    public static class LengthComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    }

    public Comparator<String> getLengthComparator() {
        return new LengthComparator();
    }


    public class AlphabetComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }
    }

    public Comparator<String> getAlphabetComparator() {
        return new AlphabetComparator();
    }

    @Test
    public void StringLengthMax() {
        Comparator<String> sc = getLengthComparator();
        MaxArrayDeque<String> test = new MaxArrayDeque<>(sc);

        test.addFirst("ajk");
        test.addFirst("emn");
        test.addLast("pjql");

        String maxLength = test.max();
        System.out.println(maxLength);
        assertEquals("pjql", maxLength);

        Comparator<String> ic = getAlphabetComparator();

        String aphaFirst = test.max(ic);
        System.out.println(aphaFirst);
        assertEquals("ajk", aphaFirst);
    }
}
