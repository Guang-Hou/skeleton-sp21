package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

public class keyboardCharInput implements CharInput {
    @Override
    public boolean hasNextChar() {
        return true;
    }

    @Override
    public char getNextChar() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }
}
