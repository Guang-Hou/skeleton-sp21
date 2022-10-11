package byow.Core;

import byow.Input.CharInput;
import byow.Input.keyboardCharInput;
import byow.Input.stringCharInput;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.nio.file.Paths;
import java.util.Random;

public class Engine {
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final File GAMEFOLDER = Paths.get(CWD.getPath(), ".gameData").toFile();
    private static final File RANDFILE = Paths.get(GAMEFOLDER.getPath(), "rand.txt").toFile();
    private static final File TERFILE = Paths.get(GAMEFOLDER.getPath(), "ter.txt").toFile();
    private static final File WORLDFILE = Paths.get(GAMEFOLDER.getPath(), "world.txt").toFile();
    private static final File PLAYERLOCATIONFILE = Paths.get(
            GAMEFOLDER.getPath(), "playerLocation.txt").toFile();
    protected static final int WIDTH = 80;
    protected static final int HEIGHT = 30;
    private TERenderer ter;
    private static TETile[][] world;
    private static Random rand;
    private Point playerLocation;
    private TETile playerTile = Tileset.MARIO;

    /**
     * The constructor initialized the world tile[][] with NOTHING tiles.
     */
    public Engine() {
        ter = new TERenderer();
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * This method is used for the user to explore the world interactively with keyboard input.
     * It calls initializeScreen() to prompt the user for several game options.
     * Then it calls handleInput() to process user input from keyboard to play the game.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        initializeScreen();
        CharInput keyboardChar = new keyboardCharInput();
        handleInput(keyboardChar);
        System.exit(0);      // what is the other alternatives, will this cause autograder problem?
    }

    /**
     * Method used for the game to interact with a string input.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        CharInput charInput = new stringCharInput(input);
        handleInput(charInput);
        return world;
    }

    /**
     * This method handless a CharInput interface type input.
     * If the input is a game option selection,
     * it will delegate the handling to loadWorld(), getSeed(), buildWorld() and saveWorld().
     * If the input is not a game option selection,
     * it will delegate the handling to handleMovement() to move the player in the world.
     * @param input The CharInput type input from user.
     */
    public void handleInput(CharInput input) {
        while (input.hasNextChar()) {
            Character c = input.getNextChar(); // already upper case
            if (c == 'L') {
                loadGame();
            } else if (c == 'N') {
                getSeed(input);
                buildWorld();
            } else if (c == ':'
                    && (!input.hasNextChar()
                    || input.getNextChar() == 'Q')) {
                saveGame();
                break;
            } else if (c == 'Q') {
                break;
            } else {
                handleMovement(c);
            }
            if (input instanceof keyboardCharInput) {
                ter.renderFrame(world);
            }
        }
    }

    /**
     * This method takes a char command and move the player position accordingly in the world.
     *
     * @param direction The char indicating the direction of the movement.
     */
    public void handleMovement(char direction) {
        if (direction == 0) {
            return;
        }
//        drawContent(10, HEIGHT - 10, "Key: " + c + "Pressed", 20);
//        StdDraw.show();
        switch (direction) {
            case 'W': {
                Point northTile = playerLocation.shift(0, 1);
                if (world[northTile.x][northTile.y].equals(Tileset.FLOOR)) {
                    world[northTile.x][northTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = northTile;
                }
                break;
            }
            case 'S': {
                Point southTile = playerLocation.shift(0, -1);
                if (world[southTile.x][southTile.y].equals(Tileset.FLOOR)) {
                    world[southTile.x][southTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = southTile;
                }
                break;
            }
            case 'A': {
                Point westTile = playerLocation.shift(-1, 0);
                if (world[westTile.x][westTile.y].equals(Tileset.FLOOR)) {
                    world[westTile.x][westTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = westTile;
                }
                break;
            }
            case 'D': {
                Point eastTile = playerLocation.shift(1, 0);
                if (world[eastTile.x][eastTile.y].equals(Tileset.FLOOR)) {
                    world[eastTile.x][eastTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = eastTile;
                }
                break;
            }
            default: {
                return;
            }
        }
    }


    /**
     * At the beginning of the game, after getting the random seed input,
     * this method fills the world with rooms
     * and hallways and setup the initial player location randomly.
     */
    public void buildWorld() {
        Rectangular.fillWorldWithRect(world, rand);
        setupPlayerInitialPoint();
    }

    /**
     * Randomly choose a FLOOR tile and change it to playerTile.
     * This method uses the same random seed from user.
     */
    public void setupPlayerInitialPoint() {
        while (true) {
            Point p = new Point(rand);
            if (world[p.x][p.y] == Tileset.FLOOR) {
                world[p.x][p.y] = playerTile;
                playerLocation = p;
                return;
            }
        }
    }

    /**
     * Initialize the screen with game options.
     * This is only used for interacting with keyboard inputs.
     */
    public void initializeScreen() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        drawContent(WIDTH / 2, HEIGHT * 3 / 4, "CS61B:  THE GAME", 45);
        drawContent(WIDTH / 2, HEIGHT / 2, "New Game (N)", 30);
        drawContent(WIDTH / 2, HEIGHT / 2 - 2, "Load Game (L)", 30);
        drawContent(WIDTH / 2, HEIGHT / 2 - 4, "Quit and save (Q)", 30);
        StdDraw.show();
    }

    /**
     * This method extract the random seed from a CharInput interface type input.
     * It is used to handle both stringCharInput class and keyboardCharInput.
     * @param input The CharInput type input from user.
     */
    public void getSeed(CharInput input) {
        if (input instanceof keyboardCharInput) {
            askUserSeed();
        }
        String strSeed = "";
        Character digit = 0;
        while (input.hasNextChar()) {
            digit = input.getNextChar(); // it is already upper case
            if (digit == 'S') {
                break;
            } else if (Character.isDigit(digit)) {
                strSeed += digit;
                if (input instanceof keyboardCharInput) {
                    drawUserSeed(strSeed);
                }
            } else {
                continue;
            }
        }
        long seed = Long.parseLong(strSeed);
        rand = new Random(seed);
    }

    /**
     * Display instructions for entering the seed number.
     */
    public void askUserSeed() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        drawContent(WIDTH / 2, HEIGHT * 3 / 4,
                "Please enter a number to randomly generate the world.", 45);
        drawContent(WIDTH / 2, HEIGHT / 2,
                "Press S to finish entering the seed.", 30);
        StdDraw.show();
    }

    /**
     * Display the user entered seed digits on the screen.
     * @param strSeed The user entered digits in a string.
     */
    public void drawUserSeed(String strSeed) {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        drawContent(WIDTH / 2, HEIGHT * 3 / 4,
                "Please enter a number to randomly generate the world.", 45);
        drawContent(WIDTH / 2, HEIGHT / 2,
                "Press S to finish entering the seed.", 30);
        drawContent(WIDTH / 2, HEIGHT / 2 - 4, strSeed, 30);
        StdDraw.show();
    }

    /**
     * Helper function to draw string contents on the screen.
     * @param x the center x-coordinate of the content
     * @param y the center y-coordinate of the content
     * @param s The content string.
     * @param fontSize The font size of the content in the screen.
     */
    public void drawContent(int x, int y, String s, int fontSize) {
        Font font = new Font("Monaco", Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.text(x, y, s);
    }

    /**
     * Persistent the game state by saving instance variables into files.
     */
    public void saveGame() {
        if (!GAMEFOLDER.exists()) {
            GAMEFOLDER.mkdir();
        }
        FileUtils.writeObject(RANDFILE, rand);
        FileUtils.writeObject(TERFILE, ter);
        FileUtils.writeObject(WORLDFILE, world);
        FileUtils.writeObject(PLAYERLOCATIONFILE, playerLocation);
    }

    /**
     * Load the game state by reading four instance variables from files.
     */
    public void loadGame() {
        rand = FileUtils.readObject(RANDFILE, Random.class);
        ter = FileUtils.readObject(TERFILE, TERenderer.class);
        world = FileUtils.readObject(WORLDFILE, TETile[][].class);
        playerLocation = FileUtils.readObject(PLAYERLOCATIONFILE, Point.class);
    }

//    public static void main(String[] args) {
//        Engine game = new Engine();
//        String input = "N252798SWD:Q";
//        game.interactWithInputString(input);
//    }

}
