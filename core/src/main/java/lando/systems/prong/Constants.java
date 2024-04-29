package lando.systems.prong;

import com.badlogic.gdx.math.Vector2;

public class Constants {
    // NOTE - box2d operates on a 'meter' scale, so 1 unit = 1 meter
    //  biggest difference from 'normal': world is *not* measured in pixels
    public static final float WORLD_WIDTH  = 80;
    public static final float WORLD_HEIGHT = 48;

    // world forces
    public static final Vector2 GRAVITY = new Vector2(0, -10);

    // box2d step settings
    public static final float TIME_STEP = 1/60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    // collision filter categories
    public static final short CAT_ARENA  = 0x0001;
    public static final short CAT_PADDLE = 0x0002;
    public static final short CAT_PRONG  = 0x0004;
    public static final short CAT_BALL   = 0x0008;
}
