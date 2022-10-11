package byow.Input;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;

/**
 * A class implementing CharInput interface. It handles user inputs from keyboard.
 */
public class KeyboardCharInput implements CharInput {
    @Override
    public boolean hasNextChar() {
        return true;
    }

    @Override
    public char getNextChar() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

    /**
     * Get keyboard input and at the same time display HUD of the tile where mouse is located.
     * @param ter The TERender for rendering the world.
     * @param world The TETile[][] world.
     * @return The user input Char.
     */
    public char getNextCharAndDisplayHUD(TERenderer ter, TETile[][] world) {
        while (true) {
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();

            if (0 < x && x < world.length && 0 < y && y < world[0].length) {
                ter.renderFrame(world);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.textLeft(1, world[0].length - 1, world[(int) x][(int) y].description());
                StdDraw.show();
            }

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }
}
