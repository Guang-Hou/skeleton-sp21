package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;
    private static final int HEXLENGTH = 4;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        addHexTesselation(world, WIDTH / 2, HEIGHT - 1);

        ter.renderFrame(world);
    }

    /**
     * Draw a hexagon tesselation.
     * @param tiles The tiles world represented by an array, to be filled with hexagon tesselation.
     * @param topXPos The top hexagon's first row starting x position.
     * @param topYPos The top hexagon's first row starting y position.
     */
    public static void addHexTesselation(TETile[][] tiles, int topXPos, int topYPos) {
        int numOfHexInCol;
        int xStart, yStart;

        // draw left part, i = 0 corresponds to the middle column
        for (int i = 0; i < HEXLENGTH; i += 1) {
            numOfHexInCol = HEXLENGTH + HEXLENGTH - 1 - i;
            xStart = topXPos - (HEXLENGTH - 1 + HEXLENGTH) * i;
            yStart = topYPos - HEXLENGTH * i;
            drawTesselationColumn(tiles, numOfHexInCol, xStart, yStart);
        }

        // draw right part
        for (int i = 1; i < HEXLENGTH; i += 1) {
            numOfHexInCol = HEXLENGTH + HEXLENGTH - 1 - i;
            xStart = topXPos + (HEXLENGTH - 1 + HEXLENGTH) * i;
            yStart = topYPos - HEXLENGTH * i;
            drawTesselationColumn(tiles, numOfHexInCol, xStart, yStart);
        }

    }

    /**
     * Draw one column in hex tesselation.
     * @param tiles The tiles world represented by an array.
     * @param numOfHexInCol The number of hexagons in the column.
     * @param xStart The top first row's most left element's x position.
     * @param yStart The top first row's most left element's y position.
     */
    public static void drawTesselationColumn(
            TETile[][] tiles, int numOfHexInCol, int xStart, int yStart) {

        for (int i = 0; i < numOfHexInCol; i += 1) {
            int yPos = yStart - 2 * HEXLENGTH * i;
            addHexagon(tiles, xStart, yPos);
        }
    }

    /**
     * Draw a hexagon.
     * @param tiles The tiles world represented by an array, to be filled with hexagon.
     * @param xPos The first row starting x position.
     * @param yPos The first rwo starting y position.
     */
    public static void addHexagon(TETile[][] tiles, int xPos, int yPos) {
        int numInRow;
        int xStart, yStart;
        TETile tile = randomTile();

        // draw upper part
        for (int i = 0; i < HEXLENGTH; i += 1) {
            numInRow = HEXLENGTH + 2 * i;
            xStart = xPos - i;
            yStart = yPos - i;
            drawHexagonRow(tiles, numInRow, xStart, yStart, tile);
        }

        // draw lower part
        for (int i = 0; i < HEXLENGTH; i += 1) {
            numInRow = HEXLENGTH + 2 * (HEXLENGTH - 1) - 2 * i;
            xStart = xPos - (HEXLENGTH - 1) + i;
            yStart = yPos - HEXLENGTH - i;
            drawHexagonRow(tiles, numInRow, xStart, yStart, tile);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.AVATAR;
            case 3: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * Draw a row of hexagon.
     * @param tiles The world tiles represented by an array.
     * @param numInRow The number of hexagons in the row.
     * @param xStart The row's most left x position.
     * @param yStart The row's most left y posotion.
     * @param tile The tile to be used to fill the hexagon.
     */
    public static void drawHexagonRow(
            TETile[][] tiles, int numInRow, int xStart, int yStart, TETile tile) {
        int xMax = tiles.length;
        int yMax = tiles[0].length;
        int x = xStart, y = yStart;

        for (int i = 0; i < numInRow; i += 1) {
            x = xStart + i;
            if (x >= 0 && x < xMax && y >= 0 && y < yMax) {
                tiles[x][y] = tile;
            }
        }
    }

}
