package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class Position {
    protected int x;
    protected int y;
    private Engine.Direction direction;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = Engine.Direction.NOWHERE;
    }

    public Position(Random rand) {
        this(rand.nextInt(Engine.WIDTH), rand.nextInt(Engine.HEIGHT));
    }

    public Position shift(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    /**
     * Assume this position has a WALL tile. Check the direction where another rectangular can be added.
     * This position will be the tunnel connecting these two rectangular.
     * @return
     */
    public Engine.Direction checkDirection(TETile[][] world) {
        Position left = this.shift(-1, 0);
        Position right = this.shift(1, 0);
        Position upper = this.shift(0, 1);
        Position lower = this.shift(0, -1);

        if (tileAtPosition(world, left) == Tileset.NOTHING && tileAtPosition(world, right) != Tileset.NOTHING
                && tileAtPosition(world, upper) == Tileset.WALL && tileAtPosition(world, lower) == Tileset.WALL) {
            return Engine.Direction.WEST;
        }
        if (tileAtPosition(world, left) != Tileset.NOTHING && tileAtPosition(world, right) == Tileset.NOTHING
                && tileAtPosition(world, upper) == Tileset.WALL && tileAtPosition(world, lower) == Tileset.WALL) {
            return Engine.Direction.EAST;
        }
        if (tileAtPosition(world, left) == Tileset.WALL && tileAtPosition(world, right) == Tileset.WALL
                && tileAtPosition(world, upper) == Tileset.NOTHING && tileAtPosition(world, lower) != Tileset.NOTHING) {
            return Engine.Direction.NORTH;
        }
        if (tileAtPosition(world, left) == Tileset.WALL && tileAtPosition(world, right) == Tileset.WALL
                && tileAtPosition(world, upper) != Tileset.NOTHING && tileAtPosition(world, lower) == Tileset.NOTHING) {
            return Engine.Direction.NORTH;
        }
        return Engine.Direction.NOWHERE;
    }

    public TETile tileAtPosition(TETile[][] world, Position p) {
        return world[p.x][p.y];
    }

    public Engine.Direction getDirection() {
        return direction;
    }

    public void setDirection(Engine.Direction d) {
        direction = d;
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

    /**
     * Randomly find a position which is a WALL. The surrounding positions are inside the world.
     *
     * @return The valid position P.
     */
    public static Position getValidRandomPosition(TETile[][] world, Random rand) {
        while (true) {
            Position p = new Position(rand);
            if (world[p.x][p.y] != Tileset.WALL) {
                continue;
            }
            Position left = p.shift(-1, 0);
            Position right = p.shift(1, 0);
            Position upper = p.shift(0, 1);
            Position lower = p.shift(0, -1);

            if (!left.isValidPosition() || !right.isValidPosition()
                    || !upper.isValidPosition() || !lower.isValidPosition()) {
                continue;
            }

            Engine.Direction d = p.checkDirection(world);
            if (d == Engine.Direction.NOWHERE) {
                continue;
            } else {
                p.setDirection(d);
            }
            return p;
        }
    }


}
