package lando.systems.prong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.prong.Constants;
import lando.systems.prong.entities.Arena;
import lando.systems.prong.entities.Ball;
import lando.systems.prong.entities.Paddle;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class GameScreen extends BaseScreen {

    boolean drawUI = true;
    float accum = 0f;

    World world;
    Arena arena;
    Paddle paddle;
    Ball ball;
    Box2DDebugRenderer renderer;

    // hit testing
    Body hitBody;
    Vector3 contact = new Vector3();
    QueryCallback callback = (fixture) -> {
        if (fixture.testPoint(contact.x, contact.y)) {
            hitBody = fixture.getBody();
            Gdx.app.debug("Fixture hit", fixture.toString());
            return false;
        }
        return true;
    };

    public GameScreen() {
        worldCamera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        worldCamera.position.set(Constants.WORLD_WIDTH / 2f, Constants.WORLD_HEIGHT / 2f, 0);
        worldCamera.update();

        Box2D.init();
        world = new World(Constants.GRAVITY, true);
        arena = new Arena(world);
        paddle = new Paddle(world, arena);
        ball = new Ball(world);

        renderer = new Box2DDebugRenderer();
        renderer.setDrawBodies(true);
        renderer.setDrawInactiveBodies(true);
        renderer.setDrawContacts(true);
        renderer.setDrawAABBs(true);
        renderer.setDrawVelocities(true);
        renderer.setDrawJoints(true);
    }

    @Override
    public void dispose() {
        world.dispose();
        renderer.dispose();
    }

    @Override
    public void initializeUI() {
        super.initializeUI();
        //uiStage.addActor(ui);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        contact.set(x, y, 0);
        worldCamera.unproject(contact);
        Gdx.app.debug("Touch down", contact.toString());

        hitBody = null;

        world.QueryAABB(callback,
            contact.x - Box2DUtils.Settings.epsilon,
            contact.y - Box2DUtils.Settings.epsilon,
            contact.x + Box2DUtils.Settings.epsilon,
            contact.y + Box2DUtils.Settings.epsilon);

        if (hitBody == null) {
            return false;
        }

        // ignore kinematic bodies, they don't work with teh mouse joint
        if (hitBody.getType() == BodyDef.BodyType.KinematicBody) {
            return false;
        }

        // hit the arena, just report it for now
        if (arena.hasBody(hitBody)) {
            Gdx.app.log("Hit arena", "TODO - bounce ball");
            // ball.applyLinearImpulse(new Vector2(0, 10), ball.getWorldCenter(), true);
        } else if (hitBody != ball.body) {
            // hit something (not ball), create a new mouse joint and attach to the hit body
            var def = new MouseJointDef();
            def.bodyA = ball.body;
            def.bodyB = hitBody;
            def.collideConnected = true;
            def.target.set(contact.x, contact.y);
            def.maxForce = 1000.0f * hitBody.getMass();

            // NOTE - Joint.linearStiffness doesn't exist in current version, not sure how to replace
            // var stiffnessAndDamping = Joint.linearStiffness(4f, 0.7f, ball, hitBody);
            // def.dampingRatio = 0.5f;
            // def.frequencyHz = 5.0f;

            world.createJoint(def);
        }

        return true;
    }

    @Override
    public void alwaysUpdate(float delta) {
        super.alwaysUpdate(delta);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        // fixed time step
        var frameTime = Math.min(delta, 0.25f);
        accum += frameTime;
        while (accum >= Constants.TIME_STEP) {
            world.step(
                Constants.TIME_STEP,
                Constants.VELOCITY_ITERATIONS,
                Constants.POSITION_ITERATIONS);
            accum -= Constants.TIME_STEP;
        }

        // NOTE - update physics bodies after world step to avoid
        //   visible jitter from manually repositioning bodies
        paddle.update(delta);

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
            float width = worldCamera.viewportWidth;
            float height = worldCamera.viewportHeight;
            float scale = 10;
            float w = scale * (assets.gdx.getWidth() / width);
            float h = scale * (assets.gdx.getHeight() / height);
            batch.draw(assets.gdx, (width - w) / 2, (height - h) / 2, w, h);
        }
        batch.end();

        renderer.render(world, worldCamera.combined);

        if (drawUI) {
            uiStage.draw();
        }
    }
}
