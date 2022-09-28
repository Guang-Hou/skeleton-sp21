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
        Rectangular firstRec = getFirstRect();
        firstRec.draw(world);
        ter.renderFrame(world);
        expandRec(firstRec);
        ter.renderFrame(world);
    }

    public Rectangular getFirstRect() {
        while (true) {
            Rectangular r = new Rectangular(rand);
            if (r.isValid(world)) {
                return r;
            }
        }
    }

    /**
     * Expand from existing rectangular R by creating adjacent rectangulars in R's four edges.
     * This is done in a recursive way until for any rectangular, the randomly created adjacent rectangular is not valid.
     *
     * @param r The staring rectangualr R.
     */
    public void expandRec(Rectangular r) {
        // select a position from one edge, randomly create a rectangular
        // if the rectangular is valid, recursively call expandRec on this rectangular
        Position northWallTile = r.getRandTileFromNorthEdge(rand);
        Rectangular northRec = new Rectangular(northWallTile.shift(-1, 1), Rectangular.CornerType.BOTTOMLEFT, rand);
        if (northRec.isValid(world)) {
            northRec.draw(world);
            makeTunnel(northWallTile, northWallTile.shift(0, 1));
            expandRec(northRec);
        }

        Position southWallTile = r.getRandTileFromSouthEdge(rand);
        Rectangular southRec = new Rectangular(southWallTile.shift(1, -1), Rectangular.CornerType.TOPRIGHT, rand);
        if (southRec.isValid(world)) {
            southRec.draw(world);
            makeTunnel(southWallTile, southWallTile.shift(0, -1));
            expandRec(southRec);
        }

        Position westWallTile = r.getRandTileFromWestEdge(rand);
        Rectangular westRec = new Rectangular(westWallTile.shift(-1, -1), Rectangular.CornerType.BOTTOMRIGHT, rand);
        if (westRec.isValid(world)) {
            westRec.draw(world);
            makeTunnel(westWallTile, westWallTile.shift(-1, 0));
            expandRec(westRec);
        }

        Position eastWallTile = r.getRandTileFromEastEdge(rand);
        Rectangular eastRec = new Rectangular(eastWallTile.shift(1, 1), Rectangular.CornerType.TOPLEFT, rand);
        if (eastRec.isValid(world)) {
            eastRec.draw(world);
            makeTunnel(eastWallTile, eastWallTile.shift(1, 0));
            expandRec(eastRec);
        }

        return;
    }

    /**
     * Make a tunnel between two rectangular by setting their tiles into FLOOR.
     *
     * @param p1 Position of the connecting tile in the first rectangular
     * @param p2 Position of the connecting tile in the other rectangular
     */
    public static void makeTunnel(Position p1, Position p2) {
        world[p1.x][p1.y] = Tileset.FLOOR;
        world[p2.x][p2.y] = Tileset.FLOOR;
    }


    public static void main(String[] args) {
        long seed = 259876;
        Engine game = new Engine();
        game.buildWorldWithSeed(seed);
    }

}
