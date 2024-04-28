package lando.systems.prong;

import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.Application.LOG_DEBUG;
import static com.badlogic.gdx.Application.LOG_INFO;

public class Config {

    public static final String window_title = "Prong - Hundred Games 001";

    public static class Debug {
        public static boolean general = false;
        public static boolean shaders = false;
        public static boolean ui = false;
        public static boolean show_launch_screen = false;
        public static boolean show_intro_screen = true;
    }

    public static class Screen {
        public static final int window_width = 1280;
        public static final int window_height = 720;
        public static final int framebuffer_width = window_width;
        public static final int framebuffer_height = window_height;
    }

    public static void toggleDebug() {
        Debug.general = !Debug.general;
        Gdx.app.log("Config", "Debug mode: " + (Debug.general ? "ON" : "OFF"));
        Gdx.app.setLogLevel(Debug.general ? LOG_DEBUG : LOG_INFO);
    }
}
