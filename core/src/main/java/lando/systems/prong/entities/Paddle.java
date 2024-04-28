package lando.systems.prong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import lando.systems.prong.Constants;
import lando.systems.prong.utils.Box2dBodyEditorLoader;
import text.formic.Stringf;

public class Paddle {

    final Arena arena;
    final float density = 1f;
    final float friction = 0.8f;
    final float restitution = 1f;

    float speed = 30f;

    public final Body body;

    public Paddle(World world, Arena arena) {
        this.arena = arena;

        var def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(Constants.WORLD_WIDTH / 2f, 1);
        body = world.createBody(def);

        var fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        var loader = new Box2dBodyEditorLoader(Gdx.files.internal("physics/prong.b2d"));
        loader.attachFixture(body, "paddle", fixtureDef, 1f);
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
}
