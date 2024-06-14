package de.tum.cit.ase.maze;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.Properties;

public class Map {
    private float tileSize = 32; // Adjust the tile size as needed
    private int[][] grid;
    private int width;
    private int height;
    private TextureRegion wallTexture;
    private Rectangle[][] hitboxes;
    private int[] startingPoint;
    private TextureRegion floor;
    private TextureRegion exit;
    private int[] enemyStartingPoint;
    private TextureRegion[] trap;
    private Rectangle[][] trapMatrix;
    private Rectangle[][] keyMatrix;
    private Rectangle[][] exitMatrix;
    private Rectangle[][] startMatrix;
    private Rectangle[][] walkMatrix;
    private int enemyCount;
    private TextureRegion keyChest;


    public Map(FileHandle fileHandle, TextureRegion wallTexture) {
        //all textures
        this.floor = new TextureRegion(new Texture("basictiles.png"), 0, 16, 16, 16);
        this.exit = new TextureRegion(new Texture("basictiles.png"), 0, 6 * 16, 16, 16);
        //animations for the trap
        this.trap = new TextureRegion[]{
                new TextureRegion(new Texture("objects.png"), 64, 48, 16, 16),
                new TextureRegion(new Texture("objects.png"), 128, 48, 16, 16),
                new TextureRegion(new Texture("objects.png"), 144, 48, 16, 16),
                new TextureRegion(new Texture("objects.png"), 160, 48, 16, 16),
        };
        this.keyChest = new TextureRegion(new Texture("key.png"),0,0,225,225);

        this.wallTexture = wallTexture;
        //Interpreting the files
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileHandle.file()))) {
            Properties properties = new Properties();
            properties.load(bufferedReader);

            // Find the maximum coordinates to determine the map size
            for (Object key : properties.keySet()) {
                    String[] coordinates = key.toString().split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);

                    width = Math.max(width, x + 1);
                    height = Math.max(height, y + 1);

            }


            // Initialize the grid and hitboxes
            grid = new int[width][height];
            hitboxes = new Rectangle[width][height];
            trapMatrix = new Rectangle[width][height];
            keyMatrix = new Rectangle[width][height];
            exitMatrix = new Rectangle[width][height];
            startMatrix = new Rectangle[width][height];
            walkMatrix = new Rectangle[width][height];

            for (int j = 0; j < grid.length; j++) {
                for (int i = 0; i < grid[j].length; i++) {
                    grid[i][j] = 6;
                }
            }
             enemyCount = 0;
            // Populate the grid based on the properties
            for (Object key : properties.stringPropertyNames()) {
                String[] coordinates = key.toString().split(",");
                int x = Integer.parseInt(coordinates[0].trim());
                int y = Integer.parseInt(coordinates[1].trim());
                int value = Integer.parseInt(properties.getProperty(key.toString()).trim());

                grid[x][y] = value;

                if (value == 1) {
                    startingPoint = new int[2];
                    startingPoint[0] = x;
                    startingPoint[1] = y;
                }
                if (value == 4) {
                    enemyCount++;
                }

            }
            this.enemyStartingPoint = new int[enemyCount * 2];
            int counter = 0;
            //Initializing the grid
            for (Object key : properties.stringPropertyNames()) {
                if (key.toString().matches("\\bHeight=(?:100|\\d{1,2})\\b\n") || key.toString().matches("\\bWidth=(?:100|\\d{1,2})\\b\n")) {
                    continue;
                }
                String[] coordinates = key.toString().split(",");
                int x = Integer.parseInt(coordinates[0].trim());
                int y = Integer.parseInt(coordinates[1].trim());
                int value = Integer.parseInt(properties.getProperty(key.toString()).trim());

                grid[x][y] = value;

                if (value == 4) {
                        this.enemyStartingPoint[counter] = x;
                        this.enemyStartingPoint[counter + 1] = y;
                        counter += 2;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public TextureRegion computeTrapTexture(int counter) {
        //Fire animation Render counter
        switch (counter) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return trap[0];

            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return trap[1];

            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                return trap[2];

            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
                return trap[3];



            default:
                return trap[0];
        }
    }


    public Rectangle[][] getTrapMatrix() {
        return trapMatrix;
    }

    public void setTrapMatrix(Rectangle[][] trapMatrix) {
        this.trapMatrix = trapMatrix;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TextureRegion getWallTexture() {
        return wallTexture;
    }

    public void setWallTexture(TextureRegion wallTexture) {
        this.wallTexture = wallTexture;
    }

    public Rectangle[][] getHitboxes() {
        return hitboxes;
    }

    public void setHitboxes(Rectangle[][] hitboxes) {
        this.hitboxes = hitboxes;
    }

    public float getTileSize() {
        return tileSize;
    }

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
    }

    public int[] getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(int[] startingPoint) {
        this.startingPoint = startingPoint;
    }

    public static boolean rectanglesIntersect(Rectangle rect1, Rectangle rect2) {
        return rect1.x < rect2.x + rect2.width &&
                rect1.x + rect1.width > rect2.x &&
                rect1.y < rect2.y + rect2.height &&
                rect1.y + rect1.height > rect2.y;
    }

    public TextureRegion getFloor() {
        return floor;
    }

    public TextureRegion getExit() {
        return exit;
    }
    public TextureRegion[] getTrap() {
        return trap;
    }
    public TextureRegion getKeyChest(){
        return keyChest;
    }

    public int[] getEnemyStartingPoint() {
        return enemyStartingPoint;
    }

    public void setEnemyStartingPoint(int[] enemyStartingPoint) {
        this.enemyStartingPoint = enemyStartingPoint;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public Rectangle[][] getKeyMatrix() {
        return keyMatrix;
    }

    public void setKeyMatrix(Rectangle[][] keyMatrix) {
        this.keyMatrix = keyMatrix;
    }

    public Rectangle[][] getExitMatrix() {
        return exitMatrix;
    }

    public void setExitMatrix(Rectangle[][] exitMatrix) {
        this.exitMatrix = exitMatrix;
    }

    public Rectangle[][] getStartMatrix() {
        return startMatrix;
    }

    public void setStartMatrix(Rectangle[][] startMatrix) {
        this.startMatrix = startMatrix;
    }

    public Rectangle[][] getWalkMatrix() {
        return walkMatrix;
    }

    public void setWalkMatrix(Rectangle[][] walkMatrix) {
        this.walkMatrix = walkMatrix;
    }
}


