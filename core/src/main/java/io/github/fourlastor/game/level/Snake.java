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
    private final int worldWidth;
    private final int worldHeight;

    private Direction direction = Direction.NORTH;
    private Direction movementDirection = direction;
    private float stepAccumulator;
    private Vector2 growPosition;

    public Snake(Stage stage, TextureRegion snakeImage, float step, Vector2 initialPosition, int worldWidth, int worldHeight) {
        this.stage = stage;
        this.snakeImage = new TextureRegionDrawable(snakeImage);
        this.step = step;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        positions = new Array<>();
        images = new Array<>();
        addToBody(initialPosition);
    }

    public void grow() {
        Vector2 position = new Vector2();
        int count = positions.size;
        if (count > 0) {
            Vector2 last = positions.get(count - 1);
            position.set(last);
        }
        growPosition = position;
    }

    private void addToBody(Vector2 position) {
        Image image = new Image(snakeImage);
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
            if (growPosition != null) {
                addToBody(growPosition);
                growPosition = null;
            }
        }
        for (int i = 0; i < positions.size; i++) {
            Vector2 position = positions.get(i);
            images.get(i).setPosition(position.x, position.y);
        }
    }

    private void moveTowards(float x, float y) {
        movementDirection = direction;
        for (int i = positions.size - 1; i > 0; i--) {
            Vector2 previousPosition = positions.get(i - 1);
            positions.get(i).set(previousPosition);
        }
        Vector2 head = positions.get(0);
        head.add(x, y);
        if (head.x >= worldWidth) {
            head.x = 0;
        } else if (head.x < 0) {
            head.x = worldWidth - 1;
        }
        if (head.y >= worldHeight) {
            head.y = 0;
        } else if (head.y < 0) {
            head.y = worldHeight - 1;
        }
    }

    private Direction updateDirection() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && movementDirection != Direction.SOUTH) {
            return Direction.NORTH;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && movementDirection != Direction.NORTH) {
            return Direction.SOUTH;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && movementDirection != Direction.WEST) {
            return Direction.EAST;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && movementDirection != Direction.EAST) {
            return Direction.WEST;
        } else {
            return direction;
        }
    }

    public Vector2 head() {
        return positions.get(0);
    }

    public boolean contains(float x, float y) {
        return contains(x, y, false);
    }

    public boolean contains(float x, float y, boolean excludeHead) {
        int start = excludeHead ? 1 : 0;
        for (int i = start; i < positions.size; i++) {
            Vector2 position = positions.get(i);
            if (position.x == x && position.y == y) {
                return true;
            }
        }
        return false;
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
