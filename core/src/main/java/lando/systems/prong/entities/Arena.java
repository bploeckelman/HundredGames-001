package lando.systems.prong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import lando.systems.prong.Constants;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;
import text.formic.Stringf;

public class Arena {

    public final Rectangle bounds;

    // NOTE - need 1x body per wall rather than a single 'container' body
    //   otherwise ball starts 'inside' the arena body and falls through
    // TODO - might not be true, try a single body and one fixture per edge
    final Body left;
    final Body right;
    final Body top;
    final Body bottom;

    public Arena(World world) {
        // position is bottom left
        var position = new Vector2(0, 0);
        var margin = 1f;

        bounds = new Rectangle(
            position.x + margin,
            position.y + margin,
            Constants.WORLD_WIDTH - 2 * margin,
            Constants.WORLD_HEIGHT - 2 * margin);

        var def = new BodyDef();
        def.position.set(position);

        left = world.createBody(def);
        right = world.createBody(def);
        top = world.createBody(def);
        bottom = world.createBody(def);

        var edgeL = new EdgeShape() {{ set(new Vector2(bounds.x, bounds.y), new Vector2(bounds.x,  bounds.y + bounds.height)); }};
        var edgeR = new EdgeShape() {{ set(new Vector2(bounds.x + bounds.width, bounds.y), new Vector2( bounds.x + bounds.width, bounds.y + bounds.height)); }};
        var edgeT = new EdgeShape() {{ set(new Vector2(bounds.x, bounds.y + bounds.height), new Vector2( bounds.x + bounds.width, bounds.y + bounds.height)); }};
        var edgeB = new EdgeShape() {{ set(new Vector2(bounds.x, bounds.y), new Vector2(bounds.x + bounds.width, bounds.y)); }};

        left.createFixture(edgeL, 0);
        right.createFixture(edgeR, 0);
        top.createFixture(edgeT, 0);
        bottom.createFixture(edgeB, 0);

        edgeL.dispose();
        edgeR.dispose();
        edgeT.dispose();
        edgeB.dispose();
    }

    public boolean hasBody(Body body) {
        return body == left || body == right || body == top || body == bottom;
    }

    private final Rectangle boundsBody = new Rectangle();
    public void constrainBounds(Body body) {
        var fixtures = body.getFixtureList();
        if (fixtures.size == 0) return;

        Box2DUtils.aabb(body, boundsBody);
        Gdx.app.debug("Paddle", Stringf.format("bounds(%s)", boundsBody.toString()));

        var arenaLeft = bounds.x;
        var arenaRight = bounds.x + bounds.width;
        var bodyLeft = boundsBody.x;
        var bodyRight = boundsBody.x + boundsBody.width;
        var bodyHalfWidth = boundsBody.width / 2f;
        var bodyPos = body.getPosition();
        var bodyRot = body.getTransform().getRotation();

        if (bodyLeft < arenaLeft) {
            Gdx.app.debug("Arena", "constrained body: " + body + " to left edge");
            var bodyPosX = arenaLeft + bodyHalfWidth;
            body.setTransform(bodyPosX, bodyPos.y, bodyRot);
            body.setLinearVelocity(0, 0);
        }
        else if (bodyRight > arenaRight) {
            Gdx.app.debug("Arena", "constrained body: " + body + " to right edge");
            var bodyPosX = arenaRight - bodyHalfWidth;
            body.setTransform(bodyPosX, bodyPos.y, bodyRot);
            body.setLinearVelocity(0, 0);
        }
    }
}
