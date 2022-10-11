package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Random;

public class Rectangular implements Serializable {
    private static int WORLDWIDTH = Engine.WIDTH;
    private static int WORLDHEIGHT = Engine.HEIGHT;
    // The max rect dimension limit in x axis
    private static final int MAXRECTWIDTH = (int) (WORLDHEIGHT * 0.35);
    // The max rect dimension limit in y axis
    private static final int MAXRECTHEIGHT = (int) (WORLDHEIGHT * 0.35);
    private Point anchor; // bottomLeft corner as the anchor point
    private int rectWidth;
    private int rectHeight;
    // helper instance variables to simplify the codes
    private Point topLeft, topRight, bottomLeft, bottomRight;

    enum CornerType { TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT }
    enum EdgeType { TOP, BOTTOM, LEFT, RIGHT }

    /**
     * Constructor based on a corner Point CORNER, its CornerType CT and a random number RAND.
     *
     * @param corner The corner Point of the rectangular.
     * @param ct The type of the corner in the rectangular.
     * @param rand The random number for the width and height of the rectangular.
     */
    public Rectangular(Point corner, CornerType ct, Random rand) {
        rectWidth = rand.nextInt(3, MAXRECTWIDTH);
        rectHeight = rand.nextInt(3, MAXRECTHEIGHT);
        switch (ct) {
            case TOPLEFT: {
                anchor = corner.shift(0, -(rectHeight - 1));
                break;
            }
            case TOPRIGHT: {
                anchor = corner.shift(-(rectWidth - 1), -(rectHeight - 1));
                break;
            }
            case BOTTOMLEFT: {
                anchor = corner;
                break;
            }
            case BOTTOMRIGHT: {
                anchor = corner.shift(-(rectWidth - 1), 0);
                break;
            }
            default: {
                return;
            }
        }
        topLeft = anchor.shift(0, rectHeight - 1);
        topRight = anchor.shift(rectWidth - 1, rectHeight - 1);
        bottomLeft = anchor;
        bottomRight = anchor.shift(rectWidth - 1, 0);
    }

    /**
     * Constructor for a random rectangular.
     * The anchor Point is randomly selected, and
     * the rectWidth and rectHeight will be randomly selected.
     *
     * @param rand The random instance for Point, width and height.
     */
    public Rectangular(Random rand) {
        this(new Point(rand), CornerType.values()[rand.nextInt(CornerType.values().length)], rand);
    }

    /**
     * Check if the rectangular is valid inside the world.
     * If any corner is outside the world, it is not valid.
     * If there is any tile in the four edges which is not type NOTHING, it is not valid.
     * Otherwise, it is valid.
     * Call this after the Rectangular constructor and before actually draw the rectangular.
     * @param world The TETile[][] world.
     * @return Boolean if the rectangular is valid or not.
     */
    public boolean isValid(TETile[][] world) {
        if (bottomLeft.x <= 0 || bottomRight.x >= WORLDWIDTH - 1
                || bottomLeft.y <= 0 || topLeft.y >= WORLDHEIGHT - 1) {
            return false;
        }
        // check top and bottom edges
        for (int x = topLeft.x; x <= topRight.x; x++) {
            if (world[x][topLeft.y] != Tileset.NOTHING
                    || world[x][bottomLeft.y] != Tileset.NOTHING) {
                return false;
            }
        }
        // check left and right edges
        for (int y = bottomLeft.y; y <= topLeft.y; y++) {
            if (world[topLeft.x][y] != Tileset.NOTHING || world[topRight.x][y] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draw this rectangular in the world.
     * Assuming the rectangular is valid. Check this before calling this method.
     * @param world The TETile[][] world.
     */
    public void draw(TETile[][] world) {
        // draw top and bottom edges with Tileset.WALL
        for (int x = topLeft.x; x <= topRight.x; x++) {
            world[x][topLeft.y] = Tileset.WALL;
            world[x][bottomLeft.y] = Tileset.WALL;
        }
        // draw left and right edges with Tileset.WALL
        for (int y = bottomLeft.y; y <= topLeft.y; y++) {
            world[topLeft.x][y] = Tileset.WALL;
            world[topRight.x][y] = Tileset.WALL;
        }
        // draw inside with Tileset.FLOOR
        for (int x = topLeft.x + 1; x < topRight.x; x++) {
            for (int y = bottomLeft.y + 1; y < topLeft.y; y++) {
                world[x][y] = Tileset.FLOOR;
            }
        }
    }


    /**
     * Fill the world with randomly rectangulars.
     *
     * @param world The TETile[][] world.
     * @param rand The random number to create the rectangulars.
     */
    public static void fillWorldWithRect(TETile[][] world, Random rand) {
        Rectangular firstRect = drawFirstRect(world, rand);
//        firstRect.draw(world);
        expandRect(firstRect, world, rand);
    }

     /**
     * Create the first rectangular and draw it in the world.
     *
     * @param world The TETile[][] world.
     * @return The first rectangular, to be used in expanRect().
     */
    public static Rectangular drawFirstRect(TETile[][] world, Random rand) {
        while (true) {
            Rectangular r = new Rectangular(rand);
            if (r.isValid(world)) {
                r.draw(world);
                return r;
            }
        }
    }

    /**
     * Expand from existing rectangular BASERECT
     * by creating adjacent rectangulars in R's four edges.
     * This is done in a recursive way until for any rectangular,
     * until the randomly created adjacent rectangulars for a rectangular are all not valid.
     *
     * @param world The TETile[][] world.
     * @param baseRect The starting rectangualr to be expanded from.
     */
    public static void expandRect(Rectangular baseRect, TETile[][] world, Random rand) {
        // try to create a rectangular adjacent to the north edge
        Point northWallTile = baseRect.getRandTileFromEdge(rand, EdgeType.TOP);
        Rectangular northRec = new Rectangular(northWallTile.shift(-1, 1),
                Rectangular.CornerType.BOTTOMLEFT, rand);
        if (northRec.isValid(world)) {
            northRec.draw(world);
            makeTunnel(world, northWallTile, northWallTile.shift(0, 1));
            expandRect(northRec, world, rand);
        }
        // try to create a rectangular adjacent to the south edge
        Point southWallTile = baseRect.getRandTileFromEdge(rand, EdgeType.BOTTOM);
        Rectangular southRec = new Rectangular(southWallTile.shift(1, -1),
                Rectangular.CornerType.TOPRIGHT, rand);
        if (southRec.isValid(world)) {
            southRec.draw(world);
            makeTunnel(world, southWallTile, southWallTile.shift(0, -1));
            expandRect(southRec, world, rand);
        }
        // try to create a rectangular adjacent to the west edge
        Point westWallTile = baseRect.getRandTileFromEdge(rand, EdgeType.LEFT);
        Rectangular westRec = new Rectangular(westWallTile.shift(-1, -1),
                Rectangular.CornerType.BOTTOMRIGHT, rand);
        if (westRec.isValid(world)) {
            westRec.draw(world);
            makeTunnel(world, westWallTile, westWallTile.shift(-1, 0));
            expandRect(westRec, world, rand);
        }
        // try to create a rectangular adjacent to the east edge
        Point eastWallTile = baseRect.getRandTileFromEdge(rand, EdgeType.RIGHT);
        Rectangular eastRec = new Rectangular(eastWallTile.shift(1, 1),
                Rectangular.CornerType.TOPLEFT, rand);
        if (eastRec.isValid(world)) {
            eastRec.draw(world);
            makeTunnel(world, eastWallTile, eastWallTile.shift(1, 0));
            expandRect(eastRec, world, rand);
        }
    }

    /**
     * Make a tunnel between two rectangulars by setting their tiles into FLOOR.
     *
     * @param world The TETile[][] world.
     * @param p1 Point of the connecting tile in the first rectangular
     * @param p2 Point of the connecting tile in the other rectangular
     */
    public static void makeTunnel(TETile[][] world, Point p1, Point p2) {
        world[p1.x][p1.y] = Tileset.FLOOR;
        world[p2.x][p2.y] = Tileset.FLOOR;
    }

    /**
     * Randomly select a wall tile from the edge of the rectangular.
     * @param rand The random number.
     * @param et The edge type of the rectangular.
     * @return The selected wall tile location Point.
     */
    public Point getRandTileFromEdge(Random rand, EdgeType et) {
        switch (et) {
            case TOP -> {
                return new Point(rand.nextInt(topLeft.x + 1, topRight.x), topLeft.y);
            }
            case BOTTOM -> {
                return new Point(rand.nextInt(bottomLeft.x + 1, bottomRight.x), bottomLeft.y);
            }
            case LEFT -> {
                return new Point(topLeft.x, rand.nextInt(bottomLeft.y + 1, topLeft.y));
            }
            case RIGHT -> {
                return new Point(topRight.x, rand.nextInt(bottomRight.y + 1, topRight.y));
            }
            default -> {
                return null;
            }
        }
    }
}
