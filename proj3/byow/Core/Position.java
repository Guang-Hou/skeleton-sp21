package byow.Core;

import byow.TileEngine.TETile;
import java.util.Random;

public class Position {
    protected int x;
    protected int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Random rand) {
        this(rand.nextInt(Engine.WIDTH), rand.nextInt(Engine.HEIGHT));
    }

    public Position shift(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    public TETile tileAtPosition(TETile[][] world, Position p) {
        return world[p.x][p.y];
    }

    /**
     * Check if this Position is inside the world.
     *
     * @return Boolean result if this position is inside the world.
     */
    public boolean isValidPosition() {
        if (this.x < 0 || this.x >= Engine.WIDTH || this.y < 0 || this.y >= Engine.HEIGHT) {
            return false;
        }
        return true;
    }

}
