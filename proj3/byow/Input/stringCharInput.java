package byow.Input;

/**
 * A class implementing CharInput interface. It handles a predetermined string input to the game.
 */
public class stringCharInput implements CharInput{
    private String inputString;
    private int curIndex = 0;

    public stringCharInput(String inputString) {
        this.inputString = inputString;
    }

    @Override
    public boolean hasNextChar() {
        return curIndex < inputString.length();
    }

    @Override
    public char getNextChar() {
        char nextChar = inputString.charAt(curIndex);
        curIndex += 1;
        return Character.toUpperCase(nextChar);
    }
}
