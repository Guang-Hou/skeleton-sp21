package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Rectangular {
    private static final int RECTWIDTH = (int) (Engine.HEIGHT * 0.5);
    private static final int RECTHEIGHT = (int) (Engine.HEIGHT * 0.5);
    private Position anchor;
    private Engine.Direction d;
    private int rectWidth;
    private int rectHeight;
    private double area = 0;
    private Position topLeft, topRight, bottomLeft, bottomRight;

    public Rectangular(Position anchor, Engine.Direction d, Random rand) {
        this.anchor = anchor;
        this.d = d;
        this.rectWidth = rand.nextInt(3, RECTWIDTH);
        this.rectHeight = rand.nextInt(3, RECTHEIGHT);
        this.area = rectHeight * rectWidth;
        switch (d) {
            case WEST: {
                topLeft = anchor.shift(-(rectWidth - 1), rectHeight - 1);
                topRight = anchor.shift(0, rectHeight - 1);
                bottomLeft = anchor.shift(-(rectWidth - 1), 0);
                bottomRight = anchor;
            }
            case EAST: {
                topLeft = anchor;
                topRight = anchor.shift(rectWidth - 1, 0);
                bottomLeft = anchor.shift(0, -(rectHeight - 1));
                bottomRight = anchor.shift(rectWidth - 1, -(rectHeight - 1));

            }
            case NORTH: {
                topLeft = anchor.shift(0, rectWidth - 1);
                topRight = anchor;
                topRight = anchor.shift(rectHeight - 1, rectWidth - 1);
                bottomRight = anchor.shift(rectHeight - 1, 0);

            }
            case SOUTH: {
                topLeft = anchor.shift(-(rectHeight - 1), 0);
                topRight = anchor;
                bottomLeft = anchor.shift(-(rectHeight - 1), -(rectWidth - 1));
                bottomRight = anchor.shift(0, -(rectWidth - 1));
            }
        }
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


    /**
     * Add a rectangular from a position P, which is a randomly selected WALL tile.
     *
     * @param p The position of the randomly selected WALL tile.
     * @return Return true if the rect is valid and add the rect in the world. The tunnel connecting the new rect and existing rect will be changed into a floor.
     */
    public static double attachRec(TETile[][] world, Position p, Random rand) {
        Rectangular r;
        Position anchor;
        switch (p.getDirection()) {
            case WEST: {
                anchor = p.shift(-1, -1);
                r = createRec(world, anchor, Engine.Direction.EAST, rand);
                if (r != null) {
                    makeTunnel(world, p, p.shift(-1, 0));
                    return r.area;
                }
            }
            case EAST: {
                anchor = p.shift(1, 1);
                r = createRec(world, anchor, Engine.Direction.EAST, rand);
                if (r != null) {
                    makeTunnel(world, p, p.shift(1, 0));
                    return r.area;
                }
            }
            case NORTH: {
                anchor = p.shift(-1, 1);
                r = createRec(world, anchor, Engine.Direction.NORTH, rand);
                if (r != null) {
                    makeTunnel(world, p, p.shift(0, 1));
                    return r.area;
                }
            }
            case SOUTH: {
                anchor = p.shift(1, -1);
                r = createRec(world, anchor, Engine.Direction.SOUTH, rand);
                if (r != null) {
                    makeTunnel(world, p, p.shift(0, -1));
                    return r.area;
                }
            }
        }
        return 0;
    }

    /** With a valid anchor position and a valid direction, try 10 times to create a rectangular.
     * @param world
     * @param anchor
     * @param direction
     * @param rand
     * @return
     */
    public static Rectangular createRec(TETile[][] world, Position anchor, Engine.Direction direction, Random rand) {
        for (int i = 0; i < 10; i++) {
            Rectangular r = new Rectangular(anchor, direction, rand);
            if (r.isValid(world)) {
                r.draw(world);
                return r;
            }
        }
        return null;
    }

    /**
     * Make a tunnel between two rectangular by setting their tiles into FLOOR.
     *
     * @param p1 Position of the connecting tile in the first rectangular
     * @param p2 Position of the connecting tile in the other rectangular
     */
    public static void makeTunnel(TETile[][] world, Position p1, Position p2) {
        world[p1.x][p1.y] = Tileset.FLOOR;
        world[p2.x][p2.y] = Tileset.FLOOR;
    }

}
