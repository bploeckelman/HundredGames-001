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
        fixtureDef.filter.categoryBits = Constants.CAT_BALL;
        fixtureDef.filter.maskBits = Constants.CAT_ARENA | Constants.CAT_PADDLE | Constants.CAT_PRONG;

        body = world.createBody(bodyDef);
        var fixture = body.createFixture(fixtureDef);
        fixture.setUserData(UserData.builder(this).name("ball").build());

        circle.dispose();
    }
}
