package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.css.Rect;

public class Character {
    private float currentX;
    private float currentY;
    private float tileSize;
    private Rectangle hitbox;
    private TextureRegion textureStanding;
    private TextureRegion[] textureWalkW;

    private TextureRegion[] textureWalkA;

    private TextureRegion[] textureWalkS;

    private TextureRegion[] textureWalkD;
    private String walkingKeyPressed;
    private byte hearts;
    private Map gameMap;
    private int time;
    private boolean hasKey;
    private float animationTime;
    private Rectangle startingPointRectangle;
    private int startingPointRectX;
    private int startingPointRectY;
    private int gettingDamage;


    public Character(float tileSize, Map gameMap) {
        this.gettingDamage = 0;
        this.walkingKeyPressed = "C";//random default walkingKey
        this.currentX = 0;
        this.currentY = 0;
        this.tileSize = tileSize;
        this.gameMap = gameMap;
        this.hasKey = false;//character doesn't have the key at the start
        updateHitbox(this.currentX, this.currentY);
        hearts = 3;//3 hearts for start
        time = 60;//basic timer for losing health
        //all the textures for the animations
        this.textureStanding = new TextureRegion(new Texture("character.png"), 0, 0, 16, 32);
        textureWalkW = new TextureRegion[]{
                new TextureRegion(new Texture("character.png"), 0, 64, 16, 32),
                new TextureRegion(new Texture("character.png"), 16, 64, 16, 32),
                new TextureRegion(new Texture("character.png"), 32, 64, 16, 32),
                new TextureRegion(new Texture("character.png"), 48, 64, 16, 32)
        };

        textureWalkA = new TextureRegion[]{
                new TextureRegion(new Texture("character.png"), 0, 96, 16, 32),
                new TextureRegion(new Texture("character.png"), 16, 96, 16, 32),
                new TextureRegion(new Texture("character.png"), 32, 96, 16, 32),
                new TextureRegion(new Texture("character.png"), 48, 96, 16, 32)
        };

        textureWalkS = new TextureRegion[]{
                new TextureRegion(new Texture("character.png"), 0, 0, 16, 32),
                new TextureRegion(new Texture("character.png"), 16, 0, 16, 32),
                new TextureRegion(new Texture("character.png"), 32, 0, 16, 32),
                new TextureRegion(new Texture("character.png"), 48, 0, 16, 32)
        };

        textureWalkD = new TextureRegion[]{
                new TextureRegion(new Texture("character.png"), 0, 32, 16, 32),
                new TextureRegion(new Texture("character.png"), 16, 32, 16, 32),
                new TextureRegion(new Texture("character.png"), 32, 32, 16, 32),
                new TextureRegion(new Texture("character.png"), 48, 32, 16, 32)
        };
        startingPointRectX = gameMap.getStartingPoint()[0];
        startingPointRectY = gameMap.getStartingPoint()[1];
        startingPointRectangle = new Rectangle(startingPointRectX, startingPointRectY, 32, 32);
    }

    //second constructor for enemies
    public Character(float TileSize, TextureRegion textureStanding, Map gameMap) {
        this.tileSize = TileSize;
        this.textureStanding = textureStanding;
        this.gameMap = gameMap;
    }

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public float getTileSize() {
        return tileSize;
    }

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public Map getGameMap() {
        return gameMap;
    }

    public void setGameMap(Map gameMap) {
        this.gameMap = gameMap;
    }

    public byte getHearts() {
        return hearts;
    }

    public void setHearts(byte hearts) {
        this.hearts = hearts;
    }

    public byte isValidMove(float newX, float newY, Map gameMap) {
        Rectangle characterHitbox = getHitbox();
        // Check for collisions with wall rectangles
        float mapStartX = gameMap.getStartingPoint()[0] * tileSize + newX;
        float mapStartY = gameMap.getStartingPoint()[1] * tileSize + newY;
        if (mapStartX >= 0 && mapStartX + characterHitbox.width <= gameMap.getWidth() * tileSize
                && mapStartY >= 0 && mapStartY + characterHitbox.height <= gameMap.getHeight() * tileSize||this instanceof Enemy){
            for (int x = 0; x < gameMap.getGrid().length; x++) {
                for (int y = 0; y < gameMap.getGrid()[0].length; y++) {
                    //checks if the character is at the exit, has the key and returns 2 if he does
                    if (gameMap.getExitMatrix()[x][y] != null) {
                        Rectangle exitRect = gameMap.getExitMatrix()[x][y];
                        if (exitRect.overlaps(characterHitbox) && hasKey) {
                            int[][] newGrid = gameMap.getGrid();
                            newGrid[x][y] = 6;
                            gameMap.setGrid(newGrid);
                            time = 60;
                            return 2; // Valid move for character
                        }
                    }
                    if (gameMap.getHitboxes()[x][y] != null) { // Check for walls
                        Rectangle wallRect = gameMap.getHitboxes()[x][y];
                        if (characterHitbox.overlaps(wallRect)) {
                            return 0; // Collision with a wall
                        }
                    }
                    //checks if character goes into a trap
                    if (gameMap.getTrapMatrix()[x][y] != null) {
                        Rectangle trapRect = gameMap.getTrapMatrix()[x][y];
                        if (trapRect.overlaps(characterHitbox) && time <= 0 && !(this instanceof Enemy)) {//Enemies shouldn't activate traps
                            Music hit = Gdx.audio.newMusic(Gdx.files.internal("damage_taken 2.mp3"));
                            hit.play();
                            time = 60;
                            hearts -= 1;
                            gettingDamage = 4;
                            int[][] newGrid = gameMap.getGrid();
                            newGrid[x][y] = 6;
                            gameMap.setGrid(newGrid);
                            return 1; // Valid move for character
                        }
                    }
                    if (!(this instanceof Enemy)) {


                        //Checks if character gets the key
                        if (gameMap.getKeyMatrix()[x][y] != null) {
                            Rectangle keyRect = gameMap.getKeyMatrix()[x][y];
                            if (keyRect.overlaps(characterHitbox)) {
                                Music keySound = Gdx.audio.newMusic(Gdx.files.internal("keysound.mp3"));
                                keySound.play();
                                hasKey = true;
                                int[][] newGrid = gameMap.getGrid();
                                newGrid[x][y] = 6;
                                gameMap.setGrid(newGrid);
                                return 1; // Valid move for character
                            }
                        }   //Checks if the character is still in the spawn and makes it a wall if he isn't
                        if (gameMap.getStartMatrix()[x][y] != null) {
                            Rectangle startRect = gameMap.getStartMatrix()[x][y];
                            if (!startRect.overlaps(characterHitbox)) {
                                int[][] newGrid = gameMap.getGrid();
                                newGrid[x][y] = 0;
                                gameMap.setGrid(newGrid);
                                return 1; // Valid move for character
                            }
                        }
                    }
                }


            }

        return 1; // Move is valid
    }else{
            return 0;


    }

}

    public boolean isHasKey() {
        return hasKey;
    }

    public void checkIfCharacterWalksIntoEnemy(Array<Enemy> enemies) {
        for (Enemy enemy :
                enemies) {
            if (enemy.getHitbox() == null) {
                return;
            }
            //Checks if character hits enemy and if so he loses a heart
            if (getHitbox().overlaps(enemy.getHitbox()) && time <= 0) {
                Music hit = Gdx.audio.newMusic(Gdx.files.internal("damage_taken 2.mp3"));
                hit.play();
                time = 60;
                hearts -= 1;
                gettingDamage = 4;
                return;
            }

        }

    }

    public void updateHitbox(float currentX, float currentY) {
        hitbox = new Rectangle(gameMap.getStartingPoint()[0] * tileSize + currentX + 5, gameMap.getStartingPoint()[1] * tileSize + currentY + 5, tileSize / 2, tileSize / 2);

    }

    public void updateAnimationTime(float delta) {
        animationTime += delta;
    }

    public TextureRegion computeTexture() {
        if (gettingDamage == 0) {
            switch (walkingKeyPressed) {
                case "W":
                    textureStanding = getWalkingAnimation(textureWalkW, animationTime);
                    break;
                case "A":
                    textureStanding = getWalkingAnimation(textureWalkA, animationTime);
                    break;
                case "D":
                    textureStanding = getWalkingAnimation(textureWalkD, animationTime);
                    break;
                case "S":
                    textureStanding = getWalkingAnimation(textureWalkS, animationTime);
                    break;
                default://If no button is pressed
                    textureStanding = textureWalkS[0];

            }
        } else {
            textureStanding = new TextureRegion(new Texture("character.png"), 144, 144, 116, 32);
            gettingDamage--;
        }
        return textureStanding;
    }

    private TextureRegion getWalkingAnimation(TextureRegion[] walkingFrames, float stateTime) {
        int frameIndex = (int) (stateTime / 0.1f) % walkingFrames.length; // Change 0.1f to adjust animation speed
        return walkingFrames[frameIndex];
    }

    public TextureRegion getTextureStanding() {
        return textureStanding;
    }

    public void setTextureStanding(TextureRegion textureStanding) {
        this.textureStanding = textureStanding;
    }


    public String getWalkingKeyPressed() {
        return walkingKeyPressed;
    }

    public void setWalkingKeyPressed(String walkingKeyPressed) {
        this.walkingKeyPressed = walkingKeyPressed;
    }

    public float getX() {
        return currentX;
    }

    public float getY() {
        return currentY;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public TextureRegion getTexture() {
        return textureStanding;
    }

    public void setTexture(TextureRegion texture) {
        this.textureStanding = texture;
    }

    public int getTime() {
        return time;
    }

    public void decreaseTime() {
        this.time -= 1;
    }

    public int getGettingDamage() {
        return gettingDamage;
    }

    public void setGettingDamage(int gettingDamage) {
        this.gettingDamage = gettingDamage;
    }
}

