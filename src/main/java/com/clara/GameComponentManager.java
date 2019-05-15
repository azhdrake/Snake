package com.clara;

/**
 * Created by Clara. Manages game components such as the Snake, Kibble... and their interactions.
 */

public class GameComponentManager {


    private Kibble kibble;
    private Snake snake;
    private Score score;
    private Wall wall;

    protected static boolean lingeringTimer = false; // a variable to keep track of if there is a unstopped timer from a previous game session.

    /** Called every clock tick. Tell components to interact/update,
     * manage interactions, update score etc.
     * If there were more components - e.g walls, mazes,
     * different types of kibble/prizes, different scoring systems...
     * they could be managed here too
     */
    public void update() {

        snake.moveSnake();

        //Ask the snake if it is on top of the kibble

        if (snake.isThisInSnake(kibble.getSquare())) {
			//If so, tell the snake that it ate the kibble
			snake.youAteKibble();

            Square kibbleLoc;

            kibble.setLastKibEaten(kibble.getKibbleType());
            kibble.resolveLastKibble();
            do {
                kibbleLoc = kibble.moveKibble();
                if(snake.isThisInSnake(kibble.getSquare())){
                    kibble.moveKibble();
                }
                isKibbleOutOfWall();

            } while (snake.isThisInSnake(kibbleLoc));
            score.increaseScore();

		}

        // checks to see if the snake is any wall and then ends the game if so.
        for (Wall wall : SnakeGame.wallList) {
            if (snake.isThisInSnake(wall.getSquare())) {
                lingeringTimer = true;
                SnakeGame.setGameStage(SnakeGame.GAME_OVER);
                return;
            }
        }
    }

    public void newGame() {
        SnakeGame.squareSize = 50;
        SnakeGame.recalculateSquares();
        score.resetScore();
        snake.createStartSnake();
        kibble.moveKibble();
        for (Wall wall : SnakeGame.wallList) {
            wall.moveWall();
        }
        isWallOutOfSnake();
        isKibbleOutOfWall();

        // this is all to make sure you don't start with a kibble effect active.
        kibble.setLastKibEaten(Kibble.NORMAL);
        kibble.setLastColorEaten(Kibble.NORMAL);
        kibble.setFirstRound(false);
    }

    // a method to check if the kibble is in a wall and moves it if so.
    protected void isKibbleOutOfWall(){
        int i = 0;
        while(i <= SnakeGame.numberOfWalls){
            // checks if kibble is out of each wall individually, and moves it if it is, and resets the counter in case it moved the kibble into a wall it already checked.
            // when counter reaches number of walls that means the kibble has been checked against every wall and is not in any of them.
            for (Wall wall : SnakeGame.wallList) {
                if (kibble.isThisInKibble(wall.getSquare()) || snake.isThisInSnake(kibble.getSquare())) {
                    kibble.moveKibble();
                    i = 0;
                }else {
                    i += 1;
                }
            }
        }
    }

    // a method to check if the wall is spawning in a snake and move it if it is.
    // there is nothing preventing walls from spawning inside other walls. This doesn't break the game and was considered a low priority implementation.
    protected void isWallOutOfSnake(){
        int i = 0;
        while(i <= SnakeGame.numberOfWalls){
            for (Wall wall : SnakeGame.wallList) {
                if (snake.isThisInSnake(wall.getSquare())) {
                    wall.moveWall();
                    i = 0;
                }else {
                    i += 1;
                }
            }
        }

    }

    public void addKibble(Kibble kibble) {
        this.kibble = kibble;
    }

    public void addSnake(Snake snake) {
        this.snake = snake;
    }

    public void addScore(Score score) {
        this.score = score;
    }

    public void addWall(Wall wall) {this.wall = wall;}

    public Score getScore() {
        return score;
    }

    public Kibble getKibble() {
        return kibble;
    }

    public Snake getSnake() {
        return snake;
    }

    public Wall getWall() { return wall;}

}
