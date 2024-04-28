package lando.systems.prong.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import lando.systems.prong.Constants;

public class Ball {

    final float radius = 1f;

    public final Body body;

    public Ball(World world) {
        var bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
            Constants.WORLD_WIDTH / 2f,
            Constants.WORLD_HEIGHT / 2f);

        var circle = new CircleShape() {{ setRadius(radius); }};

        var fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.9f;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        circle.dispose();
    }
}