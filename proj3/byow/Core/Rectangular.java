package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Rectangular {
    private static final int RECTWIDTH = (int) (Engine.HEIGHT * 0.25);   // in x axis
    private static final int RECTHEIGHT = (int) (Engine.HEIGHT * 0.25);  // in y axis
    private Position anchor; // bottomLeft corner as the anchor point
    private int rectWidth;
    private int rectHeight;
    private double area = 0;
    private Position topLeft, topRight, bottomLeft, bottomRight;

    enum CornerType {TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT}

    /**
     * Constructor based on a corner position CORNER and its CornerType CT.
     * @param corner
     * @param ct
     */
    public Rectangular(Position corner, CornerType ct, Random rand) {
        rectWidth = rand.nextInt(3, RECTWIDTH);
        rectHeight = rand.nextInt(3, RECTHEIGHT);
        area = rectHeight * rectWidth;
        switch(ct) {
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
        }
        topLeft = anchor.shift(0, rectHeight - 1);
        topRight = anchor.shift(rectWidth - 1, rectHeight - 1);
        bottomLeft = anchor;
        bottomRight = anchor.shift(rectWidth - 1, 0);
    }

    /**
     * Constructor for a random rectangular.
     * The anchor position is randomly selected, the rectWidth and rectHeight will be randomly selected.
     * @param rand The random instance for position, width and height.
     */
    public Rectangular(Random rand) {
        this(new Position(rand), CornerType.values()[rand.nextInt(CornerType.values().length)], rand);
    }

    /**
     * Check if the rectangular is valid.
     * If any corner is outside of the world, it is not valid.
     * If there is any tile in the four edges which is not type NOTHING, it is not valid.
     * Otherwise it is valid.
     *
     * @return Boolean if the rectangular is valid or not.
     */
    public boolean isValid(TETile[][] world) {
        if (bottomLeft.x <= 0 || bottomRight.x >= Engine.WIDTH - 1
                || bottomLeft.y <= 0 || topLeft.y >= Engine.HEIGHT - 1) {
            return false;
        }
        // check top and bottom edges
        for (int x = topLeft.x; x <= topRight.x; x++) {
            if (world[x][topLeft.y] != Tileset.NOTHING || world[x][bottomLeft.y] != Tileset.NOTHING) {
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
     *
     * @param world
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

    public double getArea() {
        return area;
    }

    public Position getRandTileFromNorthEdge(Random rand) {
        return new Position(rand.nextInt(topLeft.x + 1, topRight.x), topLeft.y);
    }

    public Position getRandTileFromSouthEdge(Random rand) {
        return new Position(rand.nextInt(bottomLeft.x + 1, bottomRight.x), bottomLeft.y);
    }

    public Position getRandTileFromWestEdge(Random rand) {
        return new Position(topLeft.x, rand.nextInt(bottomLeft.y + 1, topLeft.y));
    }

    public Position getRandTileFromEastEdge(Random rand) {
        return new Position(topRight.x, rand.nextInt(bottomRight.y + 1, topRight.y));
    }

}
