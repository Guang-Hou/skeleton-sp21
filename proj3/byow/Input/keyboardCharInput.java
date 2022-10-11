package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

/**
 * A class implementing CharInput interface. It handles user inputs from keyboard.
 */
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
