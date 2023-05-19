package io.github.fourlastor.game.level;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import javax.inject.Inject;
import squidpony.squidmath.SilkRNG;

public class LevelScreen extends ScreenAdapter {

    private static final float STEP = 1f / 10f;
    private static final Color CLEAR_COLOR = new Color(0x333333ff);
    private static final int WORLD_WIDTH = 16;
    private static final int WORLD_HEIGHT = 10;
    private static final Vector2 INITIAL_POSITION = new Vector2(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

    private final SilkRNG random = new SilkRNG();
    private final Stage stage;

    private final Snake snake;
    private final Image fruit;

    @Inject
    public LevelScreen(TextureAtlas atlas) {
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        snake = new Snake(stage, atlas.findRegion("whitePixel"), STEP, INITIAL_POSITION);
        fruit = new Image(new TextureRegionDrawable(atlas.findRegion("whitePixel")).tint(Color.CORAL));
        moveFruitToRandomPosition();
        stage.addActor(fruit);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        if (snakeOnFruit()) {
            snake.grow();
            moveFruitToRandomPosition();
        }
        snake.act(delta);
        stage.act();
        stage.draw();
    }

    private void moveFruitToRandomPosition() {
        float x, y;
        do {
            x = random.between(0, WORLD_WIDTH - 1);
            y = random.between(0, WORLD_HEIGHT - 1);
        } while (snake.contains(x, y));
        fruit.setPosition(x, y);
    }

    private boolean snakeOnFruit() {
        Vector2 head = snake.head();
        return head.x == fruit.getX() && head.y == fruit.getY();
    }
}
