package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import javax.inject.Inject;

import com.kotcrab.vis.ui.widget.VisLabel;
import io.github.fourlastor.game.route.Router;
import squidpony.squidmath.SilkRNG;

public class LevelScreen extends ScreenAdapter {

    private static final float STEP = 1f / 10f;
    private static final Color CLEAR_COLOR = new Color(0x333333ff);
    private static final int WORLD_WIDTH = 16;
    private static final int WORLD_HEIGHT = 10;
    private static final float UI_WIDTH = WORLD_WIDTH * 32f;
    private static final float UI_HEIGHT = WORLD_HEIGHT * 32f;

    private final Router router;

    private final SilkRNG random = new SilkRNG();
    private final Stage stage;
    private final Stage uiStage;

    private final Snake snake;
    private final Image fruit;
    private final BitmapFont font;
    private final Music music;
    private final Sound eatSound;
    private final Sound loseSound;

    private boolean gameOver;

    @Inject
    public LevelScreen(Router router, TextureAtlas atlas) {
        this.router = router;
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        snake = new Snake(stage, atlas.findRegion("whitePixel"), STEP, new Vector2(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f), WORLD_WIDTH, WORLD_HEIGHT);
        fruit = new Image(new TextureRegionDrawable(atlas.findRegion("whitePixel")).tint(Color.CORAL));
        moveFruitToRandomPosition();
        stage.addActor(fruit);
        uiStage = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));
        font = new BitmapFont(Gdx.files.internal("fonts/quan-pixel-32.fnt"));
        music = Gdx.audio.newMusic(Gdx.files.internal("audio/music/bg.mp3"));
        eatSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/eat.ogg"));
        loseSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/lose.ogg"));
        music.setLooping(true);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        super.show();
        music.play();
    }

    @Override
    public void hide() {
        super.hide();
        music.stop();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        if (snakeOnFruit()) {
            eatSound.play();
            snake.grow();
            moveFruitToRandomPosition();
        }
        if (!gameOver && snakeOnSnake()) {
            gameOver = true;
            VisLabel label = new VisLabel(
                    "Game over, press R to restart",
                    new Label.LabelStyle(
                            font, Color.FIREBRICK
                    ));
            label.setPosition(UI_WIDTH / 2, -UI_HEIGHT / 2, Align.center);
            label.addAction(Actions.moveToAligned(UI_WIDTH / 2, UI_HEIGHT / 2, Align.center, 2));
            uiStage.addActor(label);
            loseSound.play();
        }
        if (!gameOver) {
            snake.act(delta);
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                router.goToLevel();
            }
        }
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        uiStage.getViewport().apply();
        uiStage.act();
        uiStage.draw();
    }

    private boolean snakeOnSnake() {
        Vector2 head = snake.head();
        return snake.contains(head.x, head.y, true);
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
