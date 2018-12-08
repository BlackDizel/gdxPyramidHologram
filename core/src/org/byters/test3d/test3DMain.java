package org.byters.test3d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class test3DMain extends ApplicationAdapter {

    private static final String MODEL_SHIP = "ship.g3db";
    private static final int ROTATE_SPEED = 40;

    private ModelBatch modelBatch;
    private AssetManager assets;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private Environment environment;

    private boolean loading;

    private Rectangle[] viewportMeta;
    private PerspectiveCamera[] cam;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        initViewports();
        initCams();

        assets = new AssetManager();
        assets.load(MODEL_SHIP, Model.class);
        loading = true;

    }

    private void initViewports() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        int s = Math.min(w, h) / 2;


        viewportMeta = new Rectangle[4];
        viewportMeta[0] = new Rectangle(w / 2 - s, (h-s)/2, s, s); //left
        viewportMeta[1] = new Rectangle(w / 2, (h-s)/2, s, s); //right
        viewportMeta[2] = new Rectangle((w-s)/2, h / 2, s, s); //up
        viewportMeta[3] = new Rectangle((w-s)/2, h / 2 - s, s, s); //down
    }

    private void initCams() {
        cam = new PerspectiveCamera[4];

        cam[0] = getCam(0, Vector3.X.cpy().scl(-1), 90, Vector3.X.cpy());
        cam[1] = getCam(1, Vector3.X.cpy(), 90, Vector3.X.cpy());
        cam[2] = getCam(2, Vector3.Z.cpy(), 0, null);
        cam[3] = getCam(3, Vector3.Z.cpy().scl(-1), 180, Vector3.Z.cpy());
    }

    private PerspectiveCamera getCam(int itemPos, Vector3 pos, int rotate, Vector3 rotateAxis) {
        int multiplyPos = 3;
        Rectangle rect = viewportMeta[itemPos];
        PerspectiveCamera cam = new PerspectiveCamera(67, rect.width, rect.height);
        cam.position.set(pos.scl(multiplyPos));
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;

        if (rotate != 0)
            cam.rotate(rotateAxis, rotate);


        cam.update();

        return cam;
    }

    private void doneLoading() {
        Model ship = assets.get("ship.g3db", Model.class);
        ModelInstance shipInstance = new ModelInstance(ship);
        instances.add(shipInstance);
        loading = false;
    }

    @Override
    public void render() {
        if (loading && assets.update())
            doneLoading();

        input();
        draw();
    }

    private void draw() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawCam(0);
        drawCam(1);
        drawCam(2);
        drawCam(3);
    }

    private void drawCam(int position) {

        Rectangle meta = viewportMeta[position];
        Gdx.gl.glViewport((int) meta.x, (int) meta.y, (int) meta.width, (int) meta.height);

        modelBatch.begin(cam[position]);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    private void input() {
        if (instances == null || instances.size == 0) return;
        ModelInstance item = instances.get(0);
        final float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            item.transform.rotate(Vector3.Y, ROTATE_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            item.transform.rotate(Vector3.Y, -ROTATE_SPEED * delta);
/*

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            item.transform.rotate(Vector3.Z, ROTATE_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            item.transform.rotate(Vector3.Z, -ROTATE_SPEED * delta);
*/

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            item.transform.rotate(Vector3.X, ROTATE_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            item.transform.rotate(Vector3.X, -ROTATE_SPEED * delta);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }
}
