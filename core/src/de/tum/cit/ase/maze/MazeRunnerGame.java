package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Handler;


/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    private  NativeFileChooser fileChooser;
    private  NativeFileChooserConfiguration fileChooserConfiguration;


    private  FileHandle mapFile;
    private Music backgroundMusic;
    private Music gameOverMusic;
    private Music gameMusic;
    private Music victoryMusic;
    // https://github.com/spookygames/gdx-nativefilechooser
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;


    }
    public  FileHandle getMapFile() {
        return mapFile;
    }

    public  void setMapFile(FileHandle mapFile1) {
       mapFile = mapFile1;
    }


    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.ogg"));
         gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("game_over_bad_chest.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game song.mp3"));
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("Won!.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {

        menuScreen=new MenuScreen(this);
        this.setScreen(menuScreen); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }
    public void goToGameOverMenu() {
        gameOverScreen=new GameOverScreen(this);
        this.setScreen(gameOverScreen); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }

    }
    public void goToVictoryScreen(float delta, int hearts) {
        victoryScreen = new VictoryScreen(this, delta, hearts);
        this.setScreen(victoryScreen); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }
    /**
     * Switches to the game screen.
     */
    //https://github.com/spookygames/gdx-nativefilechooser
    public void getCorrectFile(){
        fileChooserConfiguration = new NativeFileChooserConfiguration();

// Starting from user's dir
        fileChooserConfiguration.directory = Gdx.files.absolute("maps");
        fileChooserConfiguration.nameFilter = (dir, name) -> name.endsWith("properties");

        fileChooser.chooseFile(fileChooserConfiguration, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                // Do stuff with file, yay!
                mapFile = file;
            }

            @Override
            public void onCancellation() {
                // Warn user how rude it can be to cancel developer's effort
            }

            @Override
            public void onError(Exception exception) {
                // Handle error (hint: use exception type)
                System.out.println("Exception: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
    public void goToGame(int Level) {
        if(Level == 0) {
            getCorrectFile();
            this.setScreen(new GameScreen(this, mapFile, 0)); // Set the current screen to GameScreen
        }else{
            this.setScreen(new GameScreen(this, mapFile, Level));
        }
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Music getGameOverMusic() {
        return gameOverMusic;
    }

    public void setGameOverMusic(Music gameOverMusic) {
        this.gameOverMusic = gameOverMusic;
    }

    public Music getGameMusic() {
        return gameMusic;
    }

    public Music getVictoryMusic() {
        return victoryMusic;
    }

}
