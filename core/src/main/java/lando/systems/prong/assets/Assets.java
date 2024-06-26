package lando.systems.prong.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import lando.systems.prong.Config;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    public enum Load { ASYNC, SYNC }

    public static final String prefsName = "001-prong";
    public Preferences preferences;

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public GlyphLayout layout;
    public AssetManager mgr;
    public TextureAtlas atlas;
    public I18NBundle strings;
//    public InputPrompts inputPrompts;
//    public Particles particles;

    public BitmapFont font;
    public BitmapFont smallFont;
    public BitmapFont largeFont;

    public Texture gdx;
    public Texture pixel;
    public Texture noiseTexture;

    public TextureRegion pixelRegion;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram starWarsShader;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram pixelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram stereoShader;
    public ShaderProgram circleCropShader;
    public ShaderProgram cubeShader;
    public ShaderProgram simpleZoomShader;
    public ShaderProgram dreamyShader;
    public ShaderProgram flameShader;
    public ShaderProgram cooldownShader;
    public ShaderProgram influencerShader;
    public ShaderProgram goalShader;
    public ShaderProgram fogOfWarShader;
    public ShaderProgram plasmaShader;
    public ShaderProgram fogObjectShader;
    public ShaderProgram starfieldShader;
    public ShaderProgram minimapShader;
    public ShaderProgram wormholeShader;
    public ShaderProgram gridShader;

    public enum Patch {
        debug, panel, metal, glass, outline,
        glass_green, glass_yellow, glass_red, glass_blue, glass_dim, glass_active;
        public NinePatch ninePatch;
        public NinePatchDrawable drawable;
    }

    public static class NinePatches {
        public static NinePatch plain;
        public static NinePatch plain_dim;
        public static NinePatch plain_gradient;
        public static NinePatch plain_gradient_highlight_yellow;
        public static NinePatch plain_gradient_highlight_green;
        public static NinePatch plain_gradient_highlight_red;
        public static NinePatch glass;
        public static NinePatch glass_active;
        public static NinePatch glass_blue;
        public static NinePatch glass_light_blue;
        public static NinePatch glass_corner_bl;
        public static NinePatch glass_corner_br;
        public static NinePatch glass_corner_tl;
        public static NinePatch glass_corner_tr;
        public static NinePatch glass_corners;
        public static NinePatch glass_red;
        public static NinePatch glass_yellow;
        public static NinePatch glass_green;
        public static NinePatch glass_tab;
        public static NinePatch glass_dim;
        public static NinePatch metal;
        public static NinePatch metal_blue;
        public static NinePatch metal_green;
        public static NinePatch metal_yellow;
        public static NinePatch shear;
        public static NinePatch outline;
        public static NinePatch debug;
    }

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        initialized = false;

        preferences = Gdx.app.getPreferences(prefsName);

        // create a single pixel texture and associated region
        var pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixmap.drawPixel(1, 0);
            pixmap.drawPixel(0, 1);
            pixmap.drawPixel(1, 1);
            pixel = new Texture(pixmap);
        }
        pixmap.dispose();
        pixelRegion = new TextureRegion(pixel);

        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);
        layout = new GlyphLayout();

        mgr = new AssetManager();
        {
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("ui/uiskin.json", Skin.class);
            mgr.load("i18n/strings", I18NBundle.class);

            mgr.load("images/libgdx.png", Texture.class);
            mgr.load("images/noise.png", Texture.class);
            mgr.load("images/pixel.png", Texture.class);

            mgr.load("fonts/outfit-medium-20px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-40px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-80px.fnt", BitmapFont.class);
        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1;

        atlas = mgr.get("sprites/sprites.atlas");

        gdx = mgr.get("images/libgdx.png");
        pixel = mgr.get("images/pixel.png");
        noiseTexture = mgr.get("images/noise.png");
        noiseTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        noiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // asset helpers
        // ...

        // String replacement
        strings = mgr.get("i18n/strings", I18NBundle.class);

        // Fonts
        smallFont = mgr.get("fonts/outfit-medium-20px.fnt");
        font      = mgr.get("fonts/outfit-medium-40px.fnt");
        largeFont = mgr.get("fonts/outfit-medium-80px.fnt");
        font.setUseIntegerPositions(false);
        smallFont.setUseIntegerPositions(false);
        largeFont.setUseIntegerPositions(false);

//        var ttfParameterLarge = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = 40; }};
//        var ttfGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Abandoned-Bold.ttf"));
//        fontAbandoned = ttfGenerator.generateFont(ttfParameterLarge);
//        var ttfParameterMed = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = 30; }};
//        fontAbandonedMed = ttfGenerator.generateFont(ttfParameterMed);
//
//        ttfGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Treamd.ttf"));
//        fontTreasureMap = ttfGenerator.generateFont(ttfParameterLarge);
//        ttfGenerator.dispose();
//
//        var ttfParameterSmall = new FreeTypeFontGenerator.FreeTypeFontParameter() {{size = 40; borderWidth = 0f; shadowOffsetX = 2; shadowOffsetY = 2;}};
//        ttfGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/zekton.ttf"));
//        fontZektonSmall = ttfGenerator.generateFont(ttfParameterSmall);
//        fontZektonSmall.setUseIntegerPositions(false);
//        ttfGenerator.dispose();

        // Transition shaders
        blindsShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/blinds.frag");
        fadeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dissolve.frag");
        radialShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/radial.frag");
        doomShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doomdrip.frag");
        pixelizeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/pixelize.frag");
        doorwayShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doorway.frag");
        crosshatchShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/crosshatch.frag");
        rippleShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/ripple.frag");
        simpleZoomShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/simplezoom.frag");
        heartShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/heart.frag");
        stereoShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/stereo.frag");
        circleCropShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/circlecrop.frag");
        cubeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/cube.frag");
        dreamyShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dreamy.frag");

        randomTransitions = new Array<>();
        randomTransitions.addAll(
            blindsShader, fadeShader, radialShader, doomShader, pixelizeShader, doorwayShader, crosshatchShader,
            rippleShader, simpleZoomShader, heartShader, stereoShader, circleCropShader, cubeShader, dreamyShader);

        // initialize patch values
        Patch.debug.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/debug"), 2, 2, 2, 2);
        Patch.panel.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/panel"), 15, 15, 15, 15);
        Patch.glass.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/glass"), 8, 8, 8, 8);
        Patch.glass_green.ninePatch  = new NinePatch(atlas.findRegion("ninepatch/glass-green"), 8, 8, 8, 8);
        Patch.glass_yellow.ninePatch = new NinePatch(atlas.findRegion("ninepatch/glass-yellow"), 8, 8, 8, 8);
        Patch.glass_red.ninePatch    = new NinePatch(atlas.findRegion("ninepatch/glass-red"), 8, 8, 8, 8);
        Patch.glass_blue.ninePatch   = new NinePatch(atlas.findRegion("ninepatch/glass-blue"), 8, 8, 8, 8);
        Patch.glass_dim.ninePatch    = new NinePatch(atlas.findRegion("ninepatch/glass-dim"), 8, 8, 8, 8);
        Patch.glass_active.ninePatch = new NinePatch(atlas.findRegion("ninepatch/glass-active"), 8, 8, 8, 8);
        Patch.metal.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/metal"), 12, 12, 12, 12);

        Patch.debug.drawable        = new NinePatchDrawable(Patch.debug.ninePatch);
        Patch.panel.drawable        = new NinePatchDrawable(Patch.panel.ninePatch);
        Patch.glass.drawable        = new NinePatchDrawable(Patch.glass.ninePatch);
        Patch.glass_green.drawable  = new NinePatchDrawable(Patch.glass_green.ninePatch);
        Patch.glass_yellow.drawable = new NinePatchDrawable(Patch.glass_yellow.ninePatch);
        Patch.glass_dim.drawable    = new NinePatchDrawable(Patch.glass_dim.ninePatch);
        Patch.glass_active.drawable = new NinePatchDrawable(Patch.glass_active.ninePatch);
        Patch.metal.drawable        = new NinePatchDrawable(Patch.metal.ninePatch);
        Patch.glass_red.drawable    = new NinePatchDrawable(Patch.glass_red.ninePatch);
        Patch.glass_blue.drawable   = new NinePatchDrawable(Patch.glass_blue.ninePatch);

        NinePatches.plain_dim                       = new NinePatch(atlas.findRegion("ninepatch/plain-dim"),               12, 12, 12, 12);
        NinePatches.plain_gradient                  = new NinePatch(atlas.findRegion("ninepatch/plain-gradient"),           2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_yellow = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-yellow"), 2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_green  = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-green"), 2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_red    = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-red"), 2,  2,  2,  2);
        NinePatches.glass                           = new NinePatch(atlas.findRegion("ninepatch/glass"),                   12, 12, 12, 12);
        NinePatches.glass_blue                      = new NinePatch(atlas.findRegion("ninepatch/glass-blue"),              12, 12, 12, 12);
        NinePatches.glass_light_blue                = new NinePatch(atlas.findRegion("ninepatch/glass"),                   12, 12, 12, 12);
        NinePatches.glass_active                    = new NinePatch(atlas.findRegion("ninepatch/glass-active"),            12, 12, 12, 12);
        NinePatches.glass_corner_bl                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-bl"),         12, 12, 12, 12);
        NinePatches.glass_corner_br                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-br"),         12, 12, 12, 12);
        NinePatches.glass_corner_tl                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-tl"),         12, 12, 12, 12);
        NinePatches.glass_corner_tr                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-tr"),         12, 12, 12, 12);
        NinePatches.glass_corners                   = new NinePatch(atlas.findRegion("ninepatch/glass-corners"),           12, 12, 12, 12);
        NinePatches.glass_red                       = new NinePatch(atlas.findRegion("ninepatch/glass-red"),               12, 12, 12, 12);
        NinePatches.glass_yellow                    = new NinePatch(atlas.findRegion("ninepatch/glass-yellow"),            12, 12, 12, 12);
        NinePatches.glass_green                     = new NinePatch(atlas.findRegion("ninepatch/glass-green"),             12, 12, 12, 12);
        NinePatches.glass_tab                       = new NinePatch(atlas.findRegion("ninepatch/glass-tab"),               12, 12, 22, 12);
        NinePatches.glass_dim                       = new NinePatch(atlas.findRegion("ninepatch/glass-dim"),               12, 12, 22, 12);
        NinePatches.metal                           = new NinePatch(atlas.findRegion("ninepatch/metal"),                   12, 12, 12, 12);
        NinePatches.metal_blue                      = new NinePatch(atlas.findRegion("ninepatch/metal-blue"),              12, 12, 12, 12);
        NinePatches.metal_green                     = new NinePatch(atlas.findRegion("ninepatch/metal-green"),             12, 12, 12, 12);
        NinePatches.metal_yellow                    = new NinePatch(atlas.findRegion("ninepatch/metal-yellow"),            12, 12, 12, 12);
        NinePatches.shear                           = new NinePatch(atlas.findRegion("ninepatch/shear"),                   75, 75, 12, 12);
        NinePatches.outline                         = new NinePatch(atlas.findRegion("ninepatch/outline"),                 3,   3,  3,  3);
        NinePatches.debug                         = new NinePatch(atlas.findRegion("ninepatch/debug"),                 3,   3,  3,  3);

        initialized = true;
        return 1;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
    }

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = true;
        var shaderProgram = new ShaderProgram(
            Gdx.files.internal(vertSourcePath),
            Gdx.files.internal(fragSourcePath));
        var log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Config.Debug.shaders) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }
}
