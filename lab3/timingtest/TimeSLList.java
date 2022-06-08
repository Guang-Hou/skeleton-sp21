package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList();
        AList<Double> times = new AList();
        AList<Integer> opCounts = new AList();

        for (int i = 0; i < 8; i += 1) {
            int N = (int) Math.pow(2, i) * 1000;
            Ns.addLast(N);


            SLList<Integer> testGetLast = new SLList();

            for (int j = 0; j < N; j += 1) {
                testGetLast.addLast(j);
            }

            int M = 10000;
            Stopwatch sw = new Stopwatch();

            for (int k = 0; k < M; k += 1) {
                int l = testGetLast.getLast();
            }

            double timeInSeconds = sw.elapsedTime();

            times.addLast(timeInSeconds);
            opCounts.addLast(M);
        }
        printTimingTable(Ns, times, opCounts);
    }

}
