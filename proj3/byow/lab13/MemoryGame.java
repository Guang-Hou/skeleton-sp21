package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!",
        "I believe in you!", "You got this!", "You're a star!", "Go Bears!",
        "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();

    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < n; i += 1) {
            char randomChar = CHARACTERS[rand.nextInt(CHARACTERS.length)];
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        // If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.CYAN);

        StdDraw.text(width / 2, height / 2, s);

        if (!gameOver) {
            StdDraw.text(4, height - 2, "Round: " + round);
            if (!playerTurn) {
                StdDraw.text(width / 2, height - 2, "Watch!");
            } else {
                StdDraw.text(width / 2, height - 2, "Type!");
            }
            StdDraw.text(width * 4 / 5, height - 2,
                    ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);

            StdDraw.text(0, height - 3, "_".repeat(width * 2));
        }

        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        playerTurn = false;
        for (int i = 0; i < letters.length(); i += 1) {
            char c = letters.charAt(i);
            drawFrame(Character.toString(c));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        playerTurn = true;
        // Read n letters of player input
        StringBuilder userInput = new StringBuilder();
        int i = 0;
        while (i < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                userInput.append(c);
                drawFrame(userInput.toString());
                i += 1;
            }
        }
        return userInput.toString();
    }

    public void startGame() {
        while (!gameOver) {
            while (true) {
                round += 1;
                drawFrame("Round: " + round);
                StdDraw.pause(1000);

                String randomString = generateRandomString(round);
                flashSequence(randomString);
                String userInput = solicitNCharsInput(round);
                if (!userInput.equals(randomString)) {
                    gameOver = true;
                    break;
                }
            }
        }

        drawFrame("GAME OVER.");
    }
}
