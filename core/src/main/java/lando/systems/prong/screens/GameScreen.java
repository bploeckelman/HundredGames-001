package lando.systems.prong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends BaseScreen {

    boolean drawUI = true;
    float accum = 0f;

    // NOTE - box2d operates on a 'meter' scale, so 1 unit = 1 meter
    //  biggest difference from 'normal': world is *not* measured in pixels
    static final int WORLD_WIDTH = 80;
    static final int WORLD_HEIGHT = 48;

    static final Vector2 GRAVITY = new Vector2(0, -10);
    static final float TIME_STEP = 1/60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    World world;
    Arena arena;
    Paddle paddle;
    Ball ball;
    Box2DDebugRenderer renderer;

    // hit testing for mouse joint
    Body hitBody;
    Vector3 contact = new Vector3();
    QueryCallback callback = (fixture) -> {
        if (fixture.testPoint(contact.x, contact.y)) {
            hitBody = fixture.getBody();
            Gdx.app.log("Fixture hit", fixture.toString());
            return false;
        }
        return true;
    };

    class Arena {
        // NOTE - need 1x body per wall rather than a single 'container' body
        //   otherwise ball starts 'inside' the arena body and falls through
        final Body left;
        final Body right;
        final Body top;
        final Body bottom;

        Arena() {
            var margin = 1f;
            var width  = (WORLD_WIDTH  / 2f) - margin;
            var height = (WORLD_HEIGHT / 2f) - margin;

            var def = new BodyDef();
            def.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

            left = world.createBody(def);
            right = world.createBody(def);
            top = world.createBody(def);
            bottom = world.createBody(def);

            var edgeL = new EdgeShape() {{ set(new Vector2(-width, -height), new Vector2(-width,  height)); }};
            var edgeR = new EdgeShape() {{ set(new Vector2( width, -height), new Vector2( width,  height)); }};
            var edgeT = new EdgeShape() {{ set(new Vector2(-width,  height), new Vector2( width,  height)); }};
            var edgeB = new EdgeShape() {{ set(new Vector2(-width, -height), new Vector2( width, -height)); }};

            left.createFixture(edgeL, 0);
            right.createFixture(edgeR, 0);
            top.createFixture(edgeT, 0);
            bottom.createFixture(edgeB, 0);

            edgeL.dispose();
            edgeR.dispose();
            edgeT.dispose();
            edgeB.dispose();
        }

        public boolean isArenaBody(Body body) {
            return body == left || body == right || body == top || body == bottom;
        }
    }

    class Ball {
        final Body body;

        Ball() {
            var bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

            var circle = new CircleShape() {{ setRadius(2); }};
            var fixtureDef = new FixtureDef();
            fixtureDef.shape = circle;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0.9f;
            fixtureDef.restitution = 1f;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            circle.dispose();
        }
    }

    class Paddle {
        final Body body;

        Paddle() {
            var def = new BodyDef();
            def.type = BodyDef.BodyType.KinematicBody;
            def.position.set(WORLD_WIDTH / 2f, 2);

            body = world.createBody(def);

            var box = new PolygonShape();
            box.setAsBox(5, 1);

            var fixtureDef = new FixtureDef();
            fixtureDef.shape = box;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0f;
            fixtureDef.restitution = 1f;

            body.createFixture(fixtureDef);

            box.dispose();
        }

        Paddle setVelocity(float x, float y) {
            body.setLinearVelocity(x, y);
            return this;
        }
    }

    public GameScreen() {
        worldCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        worldCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        worldCamera.update();

        Box2D.init();
        world = new World(GRAVITY, true);
        arena = new Arena();
        paddle = new Paddle();
        ball = new Ball();

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
        //uiStage.addActor(titleScreenUI);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        contact.set(x, y, 0);
        worldCamera.unproject(contact);
        Gdx.app.log("Touch down", contact.toString());

        hitBody = null;

        // NOTE - not sure why the example has the 0.0001 epsilon here
        var epsilon = 0.0001f;
        world.QueryAABB(callback,
            contact.x - epsilon, contact.y - epsilon,
            contact.x + epsilon, contact.y + epsilon);

        if (hitBody == null) {
            return false;
        }

        // ignore kinematic bodies, they don't work with teh mouse joint
        if (hitBody.getType() == BodyDef.BodyType.KinematicBody) {
            return false;
        }

        // hit the arena, just report it for now
        if (arena.isArenaBody(hitBody)) {
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
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        var paddleSpeed = 10f;
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) paddle.setVelocity(-paddleSpeed, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) paddle.setVelocity( paddleSpeed, 0);
        else {
            // thought that friction would take care of this
            // but since it's a force it doesn't apply to kinematic bodies (like gravity)
            // so we'd just have to apply it manually if we want a gradual slowdown
            paddle.setVelocity(0, 0);
        }
        // manual stop if we encounter the arena bounds
        // TODO - this doesn't work correctly since it 'locks' the paddle in place
        //  (once it hits a wall, it stops and can't move again till one of these conditions no longer applies, which they never will because it's stopped)
        // TODO - push up into paddle or Arena class, maybe Arena::clampBody(Body body) or similar?
//        if (paddle.body.getPosition().x < 5
//         || paddle.body.getPosition().x > WORLD_WIDTH - 5) {
//            paddle.setVelocity(0, 0);
//        }

        // fixed time step
        var frameTime = Math.min(dt, 0.25f);
        accum += frameTime;
        while (accum >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accum -= TIME_STEP;
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
