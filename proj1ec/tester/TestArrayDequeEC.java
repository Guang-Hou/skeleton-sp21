package tester;

import static org.junit.Assert.*;
import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void testStudentArrayDeque() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                solution.addLast(randVal);
                student.addLast(randVal);
                String message = "addLast(" + randVal + ")\n";
                System.out.print(message);
                assertEquals(message, solution.removeLast(), student.removeLast());
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                solution.addFirst(randVal);
                student.addFirst(randVal);
                String message = "addFirst(" + randVal + ")\n";
                System.out.print(message);
                assertEquals(message, solution.get(0), student.get(0));
            }
            else if (operationNumber == 2) {
                // size
                int LSize = solution.size();
                int BSize = student.size();
                String message = "size() \n";
                System.out.print(message);
                assertEquals(message, LSize, BSize);
            } else if (operationNumber == 3) {
                // removeFirst
                if (solution.size() > 0) {
                    String message = "removeFirst()\n";
                    System.out.print(message);
                    assertEquals(message, solution.removeFirst(), student.removeFirst());
                }
            } else if (operationNumber == 4) {
                // removeLast
                if (solution.size() > 0) {
                    String message = "removeLast()\n";
                    System.out.print(message);
                    assertEquals(message, solution.removeLast(), student.removeLast());
                }
            } else if (operationNumber == 5) {
                // get()
                if (solution.size() > 0) {
                    int randVal = StdRandom.uniform(0, solution.size());
                    String message = "get()\n";
                    System.out.print(message);
                    assertEquals(message, solution.get(randVal), student.get(randVal));
                }
            }
        }
    }
}
