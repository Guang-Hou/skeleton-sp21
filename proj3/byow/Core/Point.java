package byow.Core;

import java.io.Serializable;
import java.util.Random;

public class Point implements Serializable {
    private static int WORLDWIDTH = Engine.WIDTH;
    private static int WORLDHEIGHT = Engine.HEIGHT;
    protected final int x;
    protected final int y;

    /**
     * Constructor using a Point's location X and Y.
     * @param x The x location.
     * @param y The y locatoin.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for randomly creating a Point.
     * @param rand The random number.
     */
    public Point(Random rand) {
        this.x = rand.nextInt(WORLDWIDTH);
        this.y = rand.nextInt(WORLDHEIGHT);
    }

    /**
     * Create a new Point by shifting from the current Point's location.
     * @param dx The location change in x axis.
     * @param dy The location change in y axis.
     * @return The new Point.
     */
    public Point shift(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }
}
