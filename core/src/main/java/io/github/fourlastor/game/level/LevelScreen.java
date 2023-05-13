package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private static final float STEP = 1f / 1.5f;

    private static final Color CLEAR_COLOR = new Color(0x333333ff);

    private final Stage stage;
    private final Image snake;

    private final Vector2 velocity = new Vector2();
    private float stepAccumulator = 0f;
    private Direction direction = Direction.NORTH;

    @Inject
    public LevelScreen(TextureAtlas atlas) {
        stage = new Stage(new FitViewport(16, 9));
        snake = new Image(atlas.findRegion("whitePixel"));
        snake.setPosition(8f, 4.5f, Align.center);
        stage.addActor(snake);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        stepAccumulator += delta;
        while (stepAccumulator >= STEP) {
            Vector2 newPos = velocity.set(direction.direction).add(snake.getX(), snake.getY());
            snake.setPosition(newPos.x, newPos.y);
            stepAccumulator -= STEP;
        }
        direction = updateDirection();
        stage.act();
        stage.draw();
    }

    private Direction updateDirection() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            return Direction.NORTH;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            return Direction.SOUTH;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            return Direction.EAST;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            return Direction.WEST;
        } else {
            return direction;
        }
    }

    private enum Direction {
        NORTH(new Vector2(0, 1)),
        EAST(new Vector2(1, 0)),
        SOUTH(new Vector2(0, -1)),
        WEST(new Vector2(-1, 0));

        final Vector2 direction;

        Direction(Vector2 direction) {
            this.direction = direction;
        }
    }
}
