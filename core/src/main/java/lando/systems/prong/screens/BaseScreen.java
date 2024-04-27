package lando.systems.prong.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kotcrab.vis.ui.VisUI;
import lando.systems.prong.Config;
import lando.systems.prong.Main;
import lando.systems.prong.assets.Assets;
import lando.systems.prong.utils.screenshake.CameraShaker;


public abstract class BaseScreen implements Disposable {

    public final Main game;
    public final Assets assets;
    public final TweenManager tween;
    public final SpriteBatch batch;
    public final Vector3 pointerPos;
    public final OrthographicCamera windowCamera;
    public boolean exitingScreen;

    public OrthographicCamera worldCamera;
    public CameraShaker screenShaker;

    protected Stage uiStage;
    public Skin skin;

    public BaseScreen() {
        this.game = Main.game;
        this.assets = game.assets;
        this.tween = game.tween;
        this.batch = assets.batch;
        this.pointerPos = new Vector3();
        this.windowCamera = game.windowCamera;
        this.exitingScreen = false;

        this.worldCamera = new OrthographicCamera();
        this.screenShaker = new CameraShaker(worldCamera);
        this.worldCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        this.worldCamera.update();

        initializeUI();
    }

    @Override
    public void dispose() {}

    /**
     * Update something in the screen even when the Time.pause_for thing is being processed
     * @param delta
     */
    public abstract void alwaysUpdate(float delta);

    public void update(float delta) {
        windowCamera.update();
        if (worldCamera != null) {
            worldCamera.update();
        }

        screenShaker.update(delta);
        uiStage.act(delta);
    }

    public abstract void renderFrameBuffers(SpriteBatch batch );

    public abstract void render(SpriteBatch batch);

    protected void initializeUI() {
        skin = VisUI.getSkin();

        StretchViewport viewport = new StretchViewport(windowCamera.viewportWidth, windowCamera.viewportHeight);
        uiStage = new Stage(viewport, batch);

        // extend and setup any per-screen ui widgets in here...
    }
}
