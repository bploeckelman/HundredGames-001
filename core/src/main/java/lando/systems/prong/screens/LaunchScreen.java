package lando.systems.prong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class LaunchScreen extends BaseScreen {

    public LaunchScreen() {
        super();
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    public void update(float dt) {
        if (!exitingScreen && Gdx.input.justTouched()){
            exitingScreen = true;
            game.setScreen(new TitleScreen(), assets.heartShader, .5f);
        }
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(new Color(0f, 0f, .12f, 1));
        OrthographicCamera camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Click to Begin", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight / 2f + assets.layout.height);
            assets.font.getData().setScale(1f);
        }
        batch.end();
    }
}
