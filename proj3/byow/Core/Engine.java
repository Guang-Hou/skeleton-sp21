package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    protected static final int WIDTH = 80;
    protected static final int HEIGHT = 30;
    private static TETile[][] world = new TETile[WIDTH][HEIGHT];
    private static double coverageArea;
    private static final double TARGETAREA = WIDTH * HEIGHT * 0.2;
    private static Random rand;

    enum Direction {NORTH, SOUTH, WEST, EAST, NOWHERE}

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        buildWorldWithSeed(28476567);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }

    public void initialize() {
        ter.initialize(WIDTH, HEIGHT);

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void buildWorldWithSeed(long seed) {
        rand = new Random(seed);

        initialize();
        addFirstRect();

        ter.renderFrame(world);

        addRandomRect();

        ter.renderFrame(world);
    }

    public void addFirstRect() {
        Position p = new Position(rand);
        //world[p.x][p.y] = Tileset.AVATAR;
        Rectangular r;

        Direction[] allDirections = Direction.values();

        for (Direction d : allDirections) {
            r = new Rectangular(p, d, rand);
            if (r.isValid(world)) {
                r.draw(world);
                coverageArea += r.getArea();
                break;
            }
        }
    }

    public void addRandomRect() {
        Position p = Position.getValidRandomPosition(world, rand);
        double area = Rectangular.attachRec(world, p, rand);
        coverageArea += area;
    }

    public static void main(String[] args) {
        long seed = 259876;
        Engine game = new Engine();
        game.buildWorldWithSeed(seed);
    }

}
