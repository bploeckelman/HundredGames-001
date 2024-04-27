package lando.systems.prong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends BaseScreen {

    boolean drawUI = true;

    public GameScreen() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void initializeUI() {
        super.initializeUI();
        //uiStage.addActor(titleScreenUI);
    }

    @Override
    public void alwaysUpdate(float delta) {
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        uiStage.act();
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            float w = assets.gdx.getWidth();
            float h = assets.gdx.getHeight();
            float width = worldCamera.viewportWidth;
            float height = worldCamera.viewportHeight;
            batch.draw(assets.gdx, (width - w) / 2, (height - h) / 2, w, h);
        }
        batch.end();

        if (drawUI) {
            uiStage.draw();
        }
    }
}
