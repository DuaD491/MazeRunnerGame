package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Enemy extends Character {

    private float moveDelay = 0.5f; // Adjust this value based on your desired delay between moves
    private float moveTimer = 0f;
    private int[] StartingPoint;
    private int randomDirection;
    private float directionX;
    private float directionY;

    public Enemy(float TileSize, TextureRegion textureStanding, Map gameMap, int[] StartingPoint) {
        super(TileSize, textureStanding, gameMap);
        this.StartingPoint = StartingPoint;
        this.directionX = 0;
        this.directionY = 0;
    }

    // Add a method to make the enemy move randomly with a delay
    public void moveRandomly( int renderCounter) {
       //Timer for movement direction change
        if(renderCounter > 150){
            randomDirection = GenerateNumber(); // 0: Up, 1: Down, 2: Left, 3: Right
            float newX = this.getCurrentX();
            float newY = this.getCurrentY();

            switch (randomDirection) {
                case 0:
                    newY += 0.1f;
                    this.setWalkingKeyPressed("W");
                    directionY = 1f;
                    directionX = 0;
                    break;
                case 1:
                    newY -= 0.1f;
                    this.setWalkingKeyPressed("S");
                    directionY = -1f;
                    directionX = 0;
                    break;
                case 2:
                    newX += 0.1f;
                    this.setWalkingKeyPressed("D");
                    directionX = 1f;
                    directionY = 0;
                    break;
                case 3:
                    newX -= 0.1f;
                    this.setWalkingKeyPressed("A");
                    directionX = -1f;
                    directionY = 0;
                    break;
            }

            // Update hitbox and check for a valid move
            this.updateHitbox(newX, newY);
            if (this.isValidMove(newX, newY, this.getGameMap())==1) {
                this.setCurrentX(newX);
                this.setCurrentY(newY);
                this.updateHitbox(this.getCurrentX(),this.getCurrentY());
            }
        }else{
            //checks for collision with wall
            float newX = getCurrentX() + directionX;
            float newY = getCurrentY() + directionY;
            this.updateHitbox(newX,newY);
            if (this.isValidMove(newX, newY, this.getGameMap())==1) {
                this.setCurrentX(newX);
                this.setCurrentY(newY);
                this.updateHitbox(this.getCurrentX(),this.getCurrentY());
            }else{
                //Inverts enemy's movement direction
                directionY *= -1;
                directionX *= -1;
                this.setCurrentY(this.getCurrentY() + directionY* 5);
                this.setCurrentX(this.getCurrentX() + directionX * 5);
                this.updateHitbox(newX,newY);
            }
            }
        }

    public int GenerateNumber(){

        return MathUtils.random(0, 3);
    }

    @Override
    public void updateHitbox(float currentX, float currentY) {
        this.setHitbox( new Rectangle(StartingPoint[0] * getTileSize() + currentX + 4 , StartingPoint[1] * getTileSize() + currentY + 5, getTileSize()/4*3, getTileSize()/3));

    }
}
