package byow.Input;

/**
 * Interface wrapper for stringCharInput and keyboardCharInput.
 */
public interface CharInput {
    boolean hasNextChar();
    char getNextChar();
}
