package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
//    public static void main(String[] args) {
//        testThreeAddThreeRemove();
//    }

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        for (int i = 0; i < 4; i += 1) {
            int rand = StdRandom.uniform(100);
            noResizing.addLast(i);
            buggyAList.addLast(i);
        }

        for (int j = 0; j < 4; j += 1) {
            int x = noResizing.removeLast();
            int y = buggyAList.removeLast();
            assertEquals(x, y);
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
                assertEquals(L.size(), B.size());
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 1) {
                // size
                int LSize = L.size();
                int BSize = B.size();
                //System.out.println("LSize: " + LSize + " , " + "BSize: " + BSize);
                assertEquals(LSize, BSize);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0) {
                    int L_Last = L.getLast();
                    int B_Last = B.getLast();
                    //System.out.println("L_Last: " + L_Last + " , " + "B_Last: " + B_Last);
                    assertEquals(L_Last, B_Last);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0) {
                    int L_reLast = L.removeLast();
                    int B_reLast = B.removeLast();
                    //System.out.println("L_reLast: " + L_reLast + " , " + "B_reLast: " + B_reLast);
                    assertEquals(L_reLast, B_reLast);
                }
            }
        }
    }

}
