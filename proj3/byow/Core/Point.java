package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

public class Point implements Serializable {
    private static int WORLDWIDTH = Engine.WIDTH;
    private static int WORLDHEIGHT = Engine.HEIGHT;
    protected final int x;
    protected final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Random rand) {
        this.x = rand.nextInt(WORLDWIDTH);
        this.y = rand.nextInt(WORLDHEIGHT);
    }

    public Point shift(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }

    public TETile tileAtPoint(TETile[][] world, Point p) {
        return world[p.x][p.y];
    }

    /**
     * Check if the Point is inside the world.
     *
     * @return Boolean result if this Point is inside the world.
     */
    public boolean isValidPoint() {
        if (this.x < 0 || this.x >= WORLDWIDTH || this.y < 0 || this.y >= WORLDHEIGHT) {
            return false;
        }
        return true;
    }

}