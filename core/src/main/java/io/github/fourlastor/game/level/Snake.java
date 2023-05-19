package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class Snake {

    private final Stage stage;
    private final Array<Vector2> positions;
    private final Array<Image> images;
    private final Drawable snakeImage;
    private final float step;

    private Direction direction = Direction.NORTH;
    private float stepAccumulator;

    public Snake(Stage stage, TextureRegion snakeImage, float step, Vector2 initialPosition) {
        this.stage = stage;
        this.snakeImage = new TextureRegionDrawable(snakeImage);
        this.step = step;
        positions = new Array<>();
        images = new Array<>();
        grow();
        positions.get(0).set(initialPosition);
    }

    public void grow() {
        Image image = new Image(snakeImage);
        Vector2 position = new Vector2();
        int count = positions.size;
        if (count > 0) {
            Vector2 last = positions.get(count - 1);
            position.set(last);
        }
        images.add(image);
        positions.add(position);
        stage.addActor(image);
    }

    public void act(float delta) {
        direction = updateDirection();
        stepAccumulator += delta;
        while (stepAccumulator >= step) {
            moveTowards(direction.value.x, direction.value.y);
            stepAccumulator -= step;
        }
        for (int i = 0; i < positions.size; i++) {
            Vector2 position = positions.get(i);
            images.get(i).setPosition(position.x, position.y);
        }
    }

    private void moveTowards(float x, float y) {
        for (int i = positions.size - 1; i > 0; i--) {
            Vector2 previousPosition = positions.get(i - 1);
            positions.get(i).set(previousPosition);
        }
        Vector2 head = positions.get(0);
        head.add(x, y);
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

    public Vector2 head() {
        return positions.get(0);
    }

    enum Direction {
        NORTH(new Vector2(0, 1)),
        EAST(new Vector2(1, 0)),
        SOUTH(new Vector2(0, -1)),
        WEST(new Vector2(-1, 0));

        final Vector2 value;

        Direction(Vector2 value) {
            this.value = value;
        }
    }
}
