package lando.systems.prong.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import lando.systems.prong.Config;
import lando.systems.prong.Main;
import org.apache.jasper.tagplugins.jstl.core.If;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
        @Override
        public GwtApplicationConfiguration getConfig () {
            // Resizable application, uses available space in browser with no padding:
//            GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
//            cfg.padVertical = 0;
//            cfg.padHorizontal = 0;
//            return cfg;
//             If you want a fixed size application, comment out the above resizable section,
//             and uncomment below:
            return new GwtApplicationConfiguration(Config.Screen.window_width, Config.Screen.window_height);
        }

        @Override
        public ApplicationListener createApplicationListener () {
            return new Main();
        }
}
