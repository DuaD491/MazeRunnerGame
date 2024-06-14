package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.event.ChangeEvent;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new StretchViewport(1920,1000);// Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI element
        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Mazerunner", game.getSkin(), "title")).padBottom(80).row();
        table.setBackground(new TextureRegionDrawable(new Texture("menuscreen.jpg")));

        // Create and add a button to go to the game screen
        TextButton lvl1GameButton = new TextButton("Level 1", game.getSkin());
        table.add(lvl1GameButton).width(300).row();
        lvl1GameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(1); // Change to the game screen when button is pressed
            }
        });
        TextButton lvl2GameButton = new TextButton("Level 2", game.getSkin());
        table.add(lvl2GameButton).width(300).row();
        lvl2GameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(2); // Change to the game screen when button is pressed
            }
        });
        TextButton lvl3GameButton = new TextButton("Level 3", game.getSkin());
        table.add(lvl3GameButton).width(300).row();
        lvl3GameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(3); // Change to the game screen when button is pressed
            }
        });
        TextButton lvl4GameButton = new TextButton("Level 4", game.getSkin());
        table.add(lvl4GameButton).width(300).row();
        lvl4GameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(4); // Change to the game screen when button is pressed
            }
        });
        TextButton lvl5GameButton = new TextButton("Level 5", game.getSkin());
        table.add(lvl5GameButton).width(300).row();
        lvl5GameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(5); // Change to the game screen when button is pressed
            }
        });
        TextButton goToGameButton = new TextButton("Choose file", game.getSkin());
        table.add(goToGameButton).width(300).row();


        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                game.goToGame(0); // Change to the game screen when button is pressed
            }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

}
