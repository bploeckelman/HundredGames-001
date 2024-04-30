package lando.systems.prong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import lando.systems.prong.Constants;
import lando.systems.prong.screens.GameScreen;
import lando.systems.prong.utils.Box2dBodyEditorLoader;
import net.dermetfan.gdx.physics.box2d.ContactAdapter;
import text.formic.Stringf;

public class Paddle {

    final GameScreen screen;
    final Arena arena;
    final float density = 1f;
    final float friction = 0.8f;
    final float restitution = 1f;

    float speed = 30f;

    public final Body body;
    public final BallProngContactListener contactListener;

    public Paddle(GameScreen screen, World world, Arena arena) {
        this.screen = screen;
        this.arena = arena;

        var def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(Constants.WORLD_WIDTH / 2f, 1);
        body = world.createBody(def);

        var fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = Constants.CAT_PADDLE;
        fixtureDef.filter.maskBits = Constants.CAT_BALL;

        var loader = new Box2dBodyEditorLoader(Gdx.files.internal("physics/prong.b2d"));
        loader.attachFixture(body, "paddle", fixtureDef, 1f,
                UserData.builder(this).name("paddle").build());

        var sensorRadius = 0.25f;
        var sensorShapeLeft   = new CircleShape() {{ setPosition(new Vector2(-7, 4)); setRadius(sensorRadius); }};
        var sensorShapeCenter = new CircleShape() {{ setPosition(new Vector2( 0, 6)); setRadius(sensorRadius); }};
        var sensorShapeRight  = new CircleShape() {{ setPosition(new Vector2( 7, 4)); setRadius(sensorRadius); }};

        var sensorDef = new FixtureDef();
        sensorDef.isSensor = true;
        sensorDef.filter.categoryBits = Constants.CAT_PRONG;
        sensorDef.filter.maskBits = Constants.CAT_BALL;

        sensorDef.shape = sensorShapeLeft;
        var sensorLeft = body.createFixture(sensorDef);
        sensorLeft.setUserData(UserData.builder(this).name("sensor-left").build());

        sensorDef.shape = sensorShapeCenter;
        var sensorCenter = body.createFixture(sensorDef);
        sensorCenter.setUserData(UserData.builder(this).name("sensor-center").build());

        sensorDef.shape = sensorShapeRight;
        var sensorRight = body.createFixture(sensorDef);
        sensorRight.setUserData(UserData.builder(this).name("sensor-right").build());

        sensorShapeLeft.dispose();
        sensorShapeCenter.dispose();
        sensorShapeRight.dispose();

        contactListener = new BallProngContactListener(world, this);
    }

    public void setVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void update(float dt) {
        // manually apply friction since it's a force that wouldn't otherwise apply to kinematic bodies
        var vel = body.getLinearVelocity();
        vel.x *= friction;
        vel.y = 0; // no vertical movement
        setVelocity(vel.x, vel.y);

        // handle input
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) setVelocity(-speed, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) setVelocity( speed, 0);

        // constrain paddle movement to within arena bounds
        var x = body.getPosition().x;
        var y = body.getPosition().y;
        arena.constrainBounds(body);
        Gdx.app.debug("Paddle",
            Stringf.format("before(%.1f, %.1f)  after(%.1f, %.1f)",
                x, y, body.getPosition().x, body.getPosition().y));
    }

    public static class BallProngContactListener extends ContactAdapter {
        public final World world;
        public final Paddle paddle;

        public WeldJoint ballWeldJoint;

        public BallProngContactListener(World world, Paddle paddle) {
            this.world = world;
            this.paddle = paddle;
        }

        @Override
        public void beginContact(Contact contact) {
            if (ballWeldJoint != null) return;

            var fixtureA = contact.getFixtureA();
            var fixtureB = contact.getFixtureB();

            var sensorA = fixtureA.isSensor();
            var sensorB = fixtureB.isSensor();
            var onlyOneSensor = sensorA ^ sensorB;
            if (!onlyOneSensor) return;

            var userDataA = (UserData) fixtureA.getUserData();
            var userDataB = (UserData) fixtureB.getUserData();

            var sensor = (sensorA ? userDataA : userDataB);
            var ball = (UserData) null;
            if      (userDataA.object instanceof Ball) ball = userDataA;
            else if (userDataB.object instanceof Ball) ball = userDataB;
            if (ball == null) return;

            Gdx.app.log("Paddle", sensor.name + " hit by " + ball.name + ", welding them together");
            var ballBody = ((Ball) ball.object).body;
            var sensorBody = ((Paddle) sensor.object).body;
            var jointDef = new WeldJointDef();
            jointDef.initialize(ballBody, sensorBody, sensorBody.getPosition());

            paddle.screen.contactCallbacks.add((params) -> {
                ballWeldJoint = (WeldJoint) world.createJoint(jointDef);
                ballWeldJoint.setUserData("ball-weld-joint");
            });
        }
    }
}
