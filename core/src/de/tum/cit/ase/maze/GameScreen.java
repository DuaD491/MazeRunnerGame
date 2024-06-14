package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.w3c.dom.Text;
import org.w3c.dom.css.Rect;


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private Map gameMap;
    private float sinusInput = 0f;
    private float CharacterX;
    private float CharacterY;
    private TextureRegion[][] walkingFrames;
    private Animation[] walkingAnimation;
    private float stateTime;
    private Character character;

    int renderCounter;
    private boolean isPaused;
    private Stage stage;
    private TextButton resumeButton;
    private TextButton menuButton;
    private FrameBuffer frameBuffer;
    private Array<Enemy> enemies;
    private int speedTimer;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    private FileHandle fileHandle;
    private ShapeRenderer rect = new ShapeRenderer();
    private TextureRegion heartTexture;
    private final float heartSize = 16;
    private final float padding = 10;
    private List<Items> itemsList;
    private float movementSpeed;
    private float lastHeartsX;
    private float lastHeartsY;
    private int trapAnimationCounter;

    public GameScreen(MazeRunnerGame game, FileHandle fileHandle, int Level) {
        trapAnimationCounter = 0;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.5f;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        //Table and Buttons for pausing the game
        Table table = new Table();
        stage.addActor(table);
        table.setFillParent(true);
        table.center();
        resumeButton = new TextButton("Resume game", game.getSkin());
        table.add(resumeButton).width(300).padBottom(20).row();
        menuButton = new TextButton("Go To Menu", game.getSkin());
        table.add(menuButton).width(300);

        menuButton.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getBackgroundMusic().setLooping(true);
                game.getBackgroundMusic().play();

                game.goToMenu(); // Change to the menu screen when button is pressed
            }
        });
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getGameMusic().setLooping(true);
                game.getGameMusic().play();
                isPaused = false; // Change to the menu screen when button is pressed
            }
        });

        enemies = new Array<>();
        renderCounter = 100;
        this.game = game;
        this.fileHandle = fileHandle;
        this.heartTexture = new TextureRegion(new Texture("objects.png"), 16 * 4, 0, 16, 16);
        game.getBackgroundMusic().stop();
        // Create and configure the camera for the game view

        /*this.CharacterX = Gdx.graphics.getWidth() / 2f;
        this.CharacterY = Gdx.graphics.getHeight() / 2f;*/
        //Animations for character
        Texture sheet = new Texture(Gdx.files.internal("character.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 68, 256);
        walkingFrames = new TextureRegion[4][4];
        if (tmp.length >= 4 && tmp[0].length >= 4) {

            for (int i = 0; i < 4; i++) {
                walkingFrames[i] = Arrays.copyOfRange(tmp[i], 0, 4);
            }
        } else {
            Gdx.app.error("MyGameScreen", "Not enough frames in the sprite sheet for walking animation.");
        }
        walkingAnimation = new Animation[4];
        for (int i = 0; i < 4; i++) {
            walkingAnimation[i] = new Animation<>(0.1f,
                    walkingFrames[i][0], walkingFrames[i][1], walkingFrames[i][2], walkingFrames[i][3]);

        }
        stateTime = 0f;
        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
        this.gameMap = createMap(Level);
        character = new Character(gameMap.getTileSize(), this.gameMap);
        rect = new ShapeRenderer();
        int counter = 0;
        //Starting point array
        for (int i = 0; i < gameMap.getEnemyCount(); i++) {
            int[] startingPointE = new int[2];
            startingPointE[0] = gameMap.getEnemyStartingPoint()[counter];
            startingPointE[1] = gameMap.getEnemyStartingPoint()[counter + 1];
            Enemy enemy = new Enemy(gameMap.getTileSize(), new TextureRegion(new Texture("mobs.png"), 0, 64, 16, 16) /* other textures */, gameMap, startingPointE);
            enemies.add(enemy);
            counter += 2;
        }
        //adding Items to itemsList
        itemsList = new ArrayList<>();

        speedTimer = 0;
        movementSpeed = 1;
        drawItems(3, 2);

    }
//

    private Map createMap(int Level) {
        // still need to adapt textureRegion for every level
        // Load properties from file or set them programmatically
        Texture wallTexture = new Texture("assets/basictiles.png");
        if(Level != 0){
        FileHandle fileHandle = Gdx.files.absolute("maps/level-" + Level + ".properties");
            return new Map(fileHandle, new TextureRegion(wallTexture, 0, 0, 16, 16));
        }


        return new Map(this.fileHandle, new TextureRegion(wallTexture, 0, 0, 16, 16));

    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        //Timer for the speedPotion
        speedTimer -= 1;
        if (speedTimer == 0) {
            movementSpeed = 1;
        }
        //Pause Menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && isPaused) {
            isPaused = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
        }

        if (isPaused) {
            game.getGameMusic().pause();
            renderPauseMenu();
            return; // Skip the rest of the render method when paused
        }

        character.checkIfCharacterWalksIntoEnemy(enemies);
        renderCounter++;
        //End game if character doesn't have hearts
        if (character.getHearts() == 0) {
            game.getGameMusic().stop();
            game.getGameOverMusic().play();
            game.goToGameOverMenu();
        }
        character.decreaseTime();
        rect.setProjectionMatrix(camera.combined);
        rect.begin(ShapeRenderer.ShapeType.Line);
        /*if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }*/
        character.updateAnimationTime(delta);
        Charactermovement(delta);
        //update character´s coordinates
        CharacterX = character.getCurrentX();
        CharacterY = character.getCurrentY();
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        float targetX = gameMap.getStartingPoint()[0] * gameMap.getTileSize() + CharacterX;
        float targetY = gameMap.getStartingPoint()[1] * gameMap.getTileSize() + CharacterY;

        // Check the distance between the character and the camera's center
        float distanceX = Math.abs(camera.position.x - targetX);
        float distanceY = Math.abs(camera.position.y - targetY);

        // Set a threshold value for the distance at which the camera should reset
        float resetThresholdX = camera.viewportWidth * 0.03f;
        float resetThresholdY = camera.viewportHeight * 0.03f;

        // If the character is close to the camera's border, reset the camera
        if (distanceX > resetThresholdX || distanceY > resetThresholdY) {
            // Interpolate the current camera position towards the target position for smooth movement
            float lerp = 0.015f; // Adjust the lerp value for desired smoothness
            camera.position.lerp(new Vector3(targetX, targetY, 0), lerp);
        }


        // Update the camera
        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin();


        drawMap(game.getSpriteBatch());
        drawHearts();
        //checks if character touches any items and draws items
        touchesItem();
        for (Items item :
                itemsList) {
            game.getSpriteBatch().draw(item.getTexture(), (int) item.getPosX(), (int) item.getPosY(), gameMap.getTileSize(), gameMap.getTileSize());
        }
        //draws character
        game.getSpriteBatch().draw(
                character.computeTexture(),
                gameMap.getStartingPoint()[0] * gameMap.getTileSize() + CharacterX,
                gameMap.getStartingPoint()[1] * gameMap.getTileSize() + CharacterY,
                24,
                48
        );
        //draw enemies
        int counter = 0;
        for (Enemy enemy : enemies) {
            enemy.moveRandomly( renderCounter);
            game.getSpriteBatch().draw(
                    enemy.getTexture(),
                    gameMap.getEnemyStartingPoint()[counter] * gameMap.getTileSize() + enemy.getCurrentX(),
                    gameMap.getEnemyStartingPoint()[counter + 1] * gameMap.getTileSize() + enemy.getCurrentY(),
                    32,
                    32
            );
            counter += 2;

        }
        //Rectangles for character hitbox
        rect.setColor(Color.RED);
        rect.rect(character.getHitbox().getX(), character.getHitbox().getY(), character.getHitbox().getWidth(), character.getHitbox().getHeight());
        rect.setColor(Color.BLUE); // You can set a different color for enemy hitboxes
        for (Enemy enemy : enemies) {
            rect.rect(enemy.getHitbox().getX(), enemy.getHitbox().getY(), enemy.getHitbox().getWidth(), enemy.getHitbox().getHeight());
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything
        stateTime += delta;
        rect.end();
    }

    public void Charactermovement(float delta){
        float newX = character.getCurrentX();
        float newY = character.getCurrentY();
        boolean keyPressed = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newY += movementSpeed;
            character.setWalkingKeyPressed("W");
            character.updateHitbox(newX, newY);
            keyPressed = true;
            if (character.isValidMove(newX, newY, gameMap) == 1) {
                character.setCurrentY(newY);
            } else if (character.isValidMove(character.getCurrentX(), character.getCurrentY(), gameMap) == 2) {
                game.getGameMusic().stop();
                game.getVictoryMusic().play();
                game.goToVictoryScreen(stateTime, character.getHearts());
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newY -= movementSpeed;
            character.setWalkingKeyPressed("S");
            character.updateHitbox(newX, newY);
            keyPressed = true;
            if (character.isValidMove(newX, newY, gameMap) == 1) {
                character.setCurrentY(newY);

            } else if (character.isValidMove(character.getCurrentX(), character.getCurrentY(), gameMap) == 2) {
                game.getGameMusic().stop();
                game.getVictoryMusic().play();
                game.goToVictoryScreen(stateTime,character.getHearts());
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX -= movementSpeed;
            character.setWalkingKeyPressed("A");
            character.updateHitbox(newX, newY);
            keyPressed = true;
            if (character.isValidMove(newX, newY, gameMap) == 1) {
                character.setCurrentX(newX);
            } else if (character.isValidMove(character.getCurrentX(), character.getCurrentY(), gameMap) == 2) {
                game.getGameMusic().stop();
                game.getVictoryMusic().play();
                game.goToVictoryScreen(stateTime,character.getHearts());
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX += movementSpeed;
            character.setWalkingKeyPressed("D");
            character.updateHitbox(newX, newY);
            keyPressed = true;
            if (character.isValidMove(newX, newY, gameMap) == 1) {
                character.setCurrentX(newX);
            } else if (character.isValidMove(character.getCurrentX(), character.getCurrentY(), gameMap) == 2) {
                game.getGameMusic().stop();
                game.getVictoryMusic().play();
                game.goToVictoryScreen(stateTime, character.getHearts());
            }

        }if(!keyPressed) character.setWalkingKeyPressed("v");
    }
    private void renderPauseMenu() {
        Gdx.input.setInputProcessor(stage);
        // Update and draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw();
    }

    private void drawHearts() {
        //Creates the HUD
        for (int i = 0; i < character.getHearts(); i++) {
            float heartX = padding + i * (heartSize + padding);
            float heartY = heartSize + 30;
            Vector3 worldCoords = new Vector3(heartX, heartY, 0);
            camera.unproject(worldCoords);
            game.getSpriteBatch().draw(heartTexture, worldCoords.x, worldCoords.y, heartSize, heartSize);
            //get the position of the last heart
            lastHeartsX = worldCoords.x;
            lastHeartsY = worldCoords.y;
        }
        //draws if the character has a key
        if (character.isHasKey()) {
            game.getSpriteBatch().draw(gameMap.getKeyChest(), lastHeartsX + padding, lastHeartsY, 16, 16);
        }
    }

    private void drawMap(SpriteBatch batch) {
        // Get the width and height of the map
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        //all matrices for key, traps, hitboxes
        Rectangle[][] rectMatrix = new Rectangle[mapWidth][mapHeight];
        Rectangle[][] trapMatrix = new Rectangle[mapWidth][mapHeight];
        Rectangle[][] keyMatrix = new Rectangle[mapWidth][mapHeight];
        Rectangle[][] exitMatrix = new Rectangle[mapWidth][mapHeight];
        Rectangle[][] startMatrix = new Rectangle[mapWidth][mapHeight];
        Rectangle[][] walkMatrix = new Rectangle[mapWidth][mapHeight];
        // Iterate through the map and draw each tile
        trapAnimationCounter++;
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                // Check if the tile is a wall (assuming 0 represents a wall)
                if (gameMap.getGrid()[x][y] == 0 || gameMap.getGrid()[x][y] == 2) {
                    // Get the position of the tile
                    float xPos = x * gameMap.getTileSize();
                    float yPos = y * gameMap.getTileSize();
                    rectMatrix[x][y] = new Rectangle(xPos, yPos, gameMap.getTileSize(), gameMap.getTileSize());

                    // Draw the wall texture
                    batch.draw(gameMap.getWallTexture(), xPos, yPos, gameMap.getTileSize(), gameMap.getTileSize());
                }
                //Checks if it is a floor, a trap, an enemy or a key and then draws floor texture
                if (gameMap.getGrid()[x][y] == 6 | gameMap.getGrid()[x][y] == 4 | gameMap.getGrid()[x][y] == 5 | gameMap.getGrid()[x][y] == 3) {
                    batch.draw(gameMap.getFloor(), x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                }

                   walkMatrix[x][y]= new Rectangle(x* gameMap.getTileSize(),y* gameMap.getTileSize(), gameMap.getTileSize()/2, gameMap.getTileSize()/2);

                //Draws trap(animation) and updates the trap matrix
                if (gameMap.getGrid()[x][y] == 3) {
                    trapMatrix[x][y] = new Rectangle(x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                    batch.draw(gameMap.computeTrapTexture(trapAnimationCounter), x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                    if (trapAnimationCounter > 32) {
                        trapAnimationCounter = 0;
                    }
                }
                //Checks if it is an Exit, draws the exit and updates the exitMatrix
                if (gameMap.getGrid()[x][y] == 2) {
                    exitMatrix[x][y] = new Rectangle(x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                    batch.draw(gameMap.getExit(), x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                }
                //Checks if it is a key, draws the key and updates keyMatrix
                if (gameMap.getGrid()[x][y] == 5) {
                    keyMatrix[x][y] = new Rectangle(x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                    batch.draw(gameMap.getKeyChest(), x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                }
                if (gameMap.getGrid()[x][y] == 1) {
                    startMatrix[x][y] = new Rectangle(x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                    batch.draw(gameMap.getExit(), x * gameMap.getTileSize(), y * gameMap.getTileSize(), gameMap.getTileSize(), gameMap.getTileSize());
                }


            }
        }
        //Updates all the matrix attributes in Map
        gameMap.setHitboxes(rectMatrix);
        gameMap.setTrapMatrix(trapMatrix);
        gameMap.setKeyMatrix(keyMatrix);
        gameMap.setExitMatrix(exitMatrix);
        gameMap.setStartMatrix(startMatrix);
        gameMap.setWalkMatrix(walkMatrix);
        //Resets the renderCounter for Trap animation
        if (renderCounter > 151) {
            renderCounter = 0;
        }
    }

    public void drawItems(int amountOfHearts, int amountOfSpeedPotions) {
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        List<Vector2> occupiedPositions = new ArrayList<>();
        //draws items randomly but checks if there is already an item in that spot
        for (int i = 0; i < amountOfHearts; i++) {
            Vector2 randomPosition = getRandomEmptyPosition(mapWidth, mapHeight, occupiedPositions);
            float itemX = randomPosition.x * gameMap.getTileSize();
            float itemY = randomPosition.y * gameMap.getTileSize();
            //draws hearts
            Items newItem = new ExtraHeart(new TextureRegion(new Texture("objects.png"), 0, 16 * 3, 16, 16), itemX, itemY);
            itemsList.add(newItem);
            occupiedPositions.add(randomPosition);
        }

        for (int i = 0; i < amountOfSpeedPotions; i++) {
            Vector2 randomPosition = getRandomEmptyPosition(mapWidth, mapHeight, occupiedPositions);
            float itemX = randomPosition.x * gameMap.getTileSize();
            float itemY = randomPosition.y * gameMap.getTileSize();
            //draws SpeedPotions
            Items newItem = new SpeedPotion(new TextureRegion(new Texture("objects.png"), 13 * 16, 0, 16, 16), itemX, itemY);
            itemsList.add(newItem);
            occupiedPositions.add(randomPosition);
        }
    }

    private Vector2 getRandomEmptyPosition(int mapWidth, int mapHeight, List<Vector2> occupiedPositions) {
        int randomX, randomY;
        //gets the positions that are empty for items
        do {
            randomX = MathUtils.random(0, mapWidth - 1);
            randomY = MathUtils.random(0, mapHeight - 1);
        } while (isPositionOccupied(randomX, randomY, occupiedPositions) || isPositionOnWall(randomX, randomY));

        return new Vector2(randomX, randomY);
    }

    private boolean isPositionOccupied(int x, int y, List<Vector2> occupiedPositions) {
        //checks if there is already an item
        for (Vector2 position : occupiedPositions) {
            if (position.x == x && position.y == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isPositionOnWall(int x, int y) {
        // checks that item doesn't spawn on wall
        return gameMap.getGrid()[x][y] != 6;
    }

    public boolean touchesItem() {
        for (Items item :
                itemsList) {
            //checks for ExtraHearts and increases health
            if (item instanceof ExtraHeart) {
                Rectangle heartRect = new Rectangle((int) item.getPosX(), (int) item.getPosY(),gameMap.getTileSize() - 8, gameMap.getTileSize() - 8);
                if (character.getHitbox().overlaps(heartRect)) {
                    itemsList.remove(item);
                    character.setHearts((byte) (character.getHearts() + 1));
                    Music heartSound = Gdx.audio.newMusic(Gdx.files.internal("life_pickup.mp3"));
                    heartSound.play();
                    return true;
                }
            }
            //checks for SpeedPotion and increases speed
            if (item instanceof SpeedPotion) {
                Rectangle itemRect = new Rectangle((int) item.getPosX(), (int) item.getPosY(),gameMap.getTileSize() - 8, gameMap.getTileSize() - 8);
                if (character.getHitbox().overlaps(itemRect)) {
                    itemsList.remove(item);
                    Music speedSound = Gdx.audio.newMusic(Gdx.files.internal("speedItem.mp3"));
                    speedSound.play();
                    movementSpeed += 1;
                    speedTimer = 300;
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    // Additional methods and logic can be added as needed for the game screen
}
