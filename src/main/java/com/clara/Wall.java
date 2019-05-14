package com.clara;

import java.util.Random;

public class Wall {

    private int wallX; //This is the square number (not pixel)
    private int wallY;  //This is the square number (not pixel)

    public Wall(){
        moveWall();
    }

    protected Square moveWall(){
        Random rng = new Random();

        wallX = rng.nextInt(SnakeGame.xSquares);
        wallY = rng.nextInt(SnakeGame.ySquares);
        return new Square(wallX, wallY);

    }

    public int getWallX() {
        return wallX;
    }

    public int getWallY() {
        return wallY;
    }


    public Square getSquare() {
        return new Square(wallX, wallY);
    }

    // a method to test if somethings in the wall. So far it hasn't been used but it has potential so I'm keeping it here for now.
    public boolean isThisInWall(Square testSquare) {
        if (wallX == testSquare.x && wallY == testSquare.y) {
            return true;
        }
        return false;
    }

}
