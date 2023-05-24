package io.github.fourlastor.game.intro;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.fourlastor.game.route.Router;
import javax.inject.Inject;

public class IntroScreen extends ScreenAdapter {

    public static final Color CLEAR_COLOR = Color.valueOf("000000");

    private final Stage stage;

    private final InputMultiplexer multiplexer;

    @Inject
    public IntroScreen(InputMultiplexer multiplexer, Router router) {
        VisUI.load(VisUI.SkinScale.X2);
        this.multiplexer = multiplexer;
        stage = new Stage();
        VisTextButton button = new VisTextButton("Start");
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                router.goToLevel();
            }
        });
        button.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f, Align.center);
        stage.addActor(button);
        stage.addActor(button);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        super.dispose();
    }

    @Override
    public void show() {
        super.show();
        multiplexer.addProcessor(stage);
    }

    @Override
    public void hide() {
        multiplexer.removeProcessor(stage);
        super.hide();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        stage.act(delta);
        stage.draw();
    }
}
