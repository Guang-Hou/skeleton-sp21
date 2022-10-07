package byow.Core;

import byow.Input.CharInput;
import byow.Input.keyboardCharInput;
import byow.Input.stringCharInput;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Random;

public class Engine {
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final File GAMEFOLDER = Paths.get(CWD.getPath(), ".gameData").toFile();
    private static final File RANDFILE = Paths.get(GAMEFOLDER.getPath(), "rand.txt").toFile();
    private static final File WORLDFILE = Paths.get(GAMEFOLDER.getPath(), "world.txt").toFile();
    private static final File playerLocationFILE = Paths.get(GAMEFOLDER.getPath(), "playerLocation.txt").toFile();
    private TERenderer ter;
    protected static final int WIDTH = 80;
    protected static final int HEIGHT = 30;
    private static TETile[][] world;
    private static Random rand;
    private Point playerLocation;
    private TETile playerTile = Tileset.MARIO;

    /**
     * The constructor initialized the world tile[][] with NOTHING tiles.
     */
    public Engine() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        initializeScreen();
        CharInput keyboardChar = new keyboardCharInput();
        handleInput(keyboardChar);
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
        // ter.renderFrame(world);
        return world;
    }

    public void buildWorld() {
        Rectangular.fillWorldWithRect(world, rand);
        setupPlayerInitialPoint();
        ter.renderFrame(world);
    }

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
     * Initialize the screen for keyboard users.
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

    public void saveWorld() {
        FileUtils.writeObject(RANDFILE, rand);
        FileUtils.writeObject(WORLDFILE, world);
        FileUtils.writeObject(playerLocationFILE, playerLocation);
    }

    public void loadWorld() {
        rand = FileUtils.readObject(RANDFILE, Random.class);
        world = FileUtils.readObject(WORLDFILE, TETile[][].class);
        playerLocation = FileUtils.readObject(playerLocationFILE, Point.class);
    }

    public void handleMovement(char c) {
        if (c == 0) {
            return;
        }
        Character direction = Character.toUpperCase(c);
//        drawContent(10, HEIGHT - 10, "Key: " + c + "Pressed", 20);
//        StdDraw.show();
        switch (direction) {
            case 'W': {
                Point northTile = playerLocation.shift(0, 1);
                if (world[northTile.x][northTile.y] == Tileset.FLOOR) {
                    world[northTile.x][northTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = northTile;
                }
                break;
            }
            case 'S': {
                Point southTile = playerLocation.shift(0, -1);
                if (world[southTile.x][southTile.y] == Tileset.FLOOR) {
                    world[southTile.x][southTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = southTile;
                }
                break;
            }
            case 'A': {
                Point westTile = playerLocation.shift(-1, 0);
                if (world[westTile.x][westTile.y] == Tileset.FLOOR) {
                    world[westTile.x][westTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = westTile;
                }
                break;
            }
            case 'D': {
                Point eastTile = playerLocation.shift(1, 0);
                if (world[eastTile.x][eastTile.y] == Tileset.FLOOR) {
                    world[eastTile.x][eastTile.y] = playerTile;
                    world[playerLocation.x][playerLocation.y] = Tileset.FLOOR;
                    playerLocation = eastTile;
                }
                break;
            }
        }
        ter.renderFrame(world);
    }

    public void handleInput(CharInput input) {
        while (input.hasNextChar()) {
            Character c = Character.toUpperCase(input.getNextChar());
            if (c == 'L') {
                loadWorld();
            } else if (c == 'N') {
                getSeed(input);
                buildWorld();
            } else if (c == ':' && (!input.hasNextChar() || Character.toUpperCase(input.getNextChar()) == 'Q')) {
                saveWorld();
                break;
            } else {
                handleMovement(c);
            }
        }
    }


    public void getSeed(CharInput input) {
        if (input instanceof keyboardCharInput) {
            askUserSeed();
        }
        String strSeed = "";
        Character digit = 0;
        while (input.hasNextChar()) {
            digit = input.getNextChar();
            if (Character.toUpperCase(digit) == 'S') {  // 'S' is the indicator for the end of the seed numbers
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

    public void askUserSeed() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        drawContent(WIDTH / 2, HEIGHT * 3 / 4, "Please enter a number to randomly generate the world.", 45);
        drawContent(WIDTH / 2, HEIGHT / 2, "Press S to finish entering the seed.", 30);
        StdDraw.show();
    }

    public void drawUserSeed(String strSeed) {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        drawContent(WIDTH / 2, HEIGHT * 3 / 4, "Please enter a number to randomly generate the world.", 45);
        drawContent(WIDTH / 2, HEIGHT / 2, "Press S to finish entering the seed.", 30);
        drawContent(WIDTH / 2, HEIGHT / 2 - 4, strSeed, 30);
        StdDraw.show();
    }

    public void drawContent(int x, int y, String s, int fontSize) {
        Font font = new Font("Monaco", Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.text(x, y, s);
    }

    public static void main(String[] args) {
//        File CWD = new File(System.getProperty("user.dir"));
//        System.out.println(CWD);
        String input = "N999SDDDWWWDDD";
        Engine game = new Engine();
        game.interactWithInputString(input);
    }
}
