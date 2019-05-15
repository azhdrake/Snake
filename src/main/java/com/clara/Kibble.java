package com.clara;

import java.util.Random;

/* In this game, Snakes eat Kibble. Feel free to rename to SnakeFood or Prize or Treats or Cake or whatever. */


public class Kibble {

	/** Identifies a random square to display a kibble
	 * Any square is ok, so long as it doesn't have any snake segments in it. 
	 * There is only one Kibble that knows where it is on the screen. When the snake eats the kibble, it doesn't disappear and
	 * get recreated, instead it moves, and then will be drawn in the new location. 
	 */
	Random rand = new Random();

	protected static final int NORMAL = 0;
	protected static final int PINK = 1;
	protected static final int BLUE = 2;
	protected static final int GREEN = 3;
	protected static final int SHRINKSQARES = 4;
	protected static final int NYOOMTIME = 5;
	protected static final int WALLMAGEDDON = 6;
	protected static final int NEGATIVESCORE = 7;

	private int kibbleX; //This is the square number (not pixel)
	private int kibbleY;  //This is the square number (not pixel)
	private int kibbleType;
	private int lastKibEaten = NORMAL;
	private int lastColorEaten = NORMAL;
	protected static boolean firstRound = true; // used to keep track of if it's the begaining of the game so the kibble effects can be neutralized.
	private boolean wallmageddonResolved = false; // used to keep track of if we are in the middle or resolving the wallmageddon kibble effect.



	public Kibble() {
		int kibType = rand.nextInt(8);
		this.kibbleType = kibType;
		moveKibble();
	}

	protected Square moveKibble(){
		// tests if it's the start of the game, and if so doesn't resolve any kibble effects.
		if (firstRound){
			lastKibEaten = NORMAL;
			lastColorEaten = NORMAL;
		}else{
			changeKibbleType();
		}
		// moves the kibble.
		Random rng = new Random();
		kibbleX = rng.nextInt(SnakeGame.xSquares);
		kibbleY = rng.nextInt(SnakeGame.ySquares);

		return new Square(kibbleX, kibbleY);
	}

	// figures out the type of kibble just eaten and resolves it.
	protected void resolveLastKibble(){

		if (lastKibEaten == PINK || lastKibEaten == BLUE || lastKibEaten == GREEN || lastKibEaten == NORMAL){
			this.setLastColorEaten(lastKibEaten);
		} else if (lastKibEaten == Kibble.SHRINKSQARES){
			shrinkSquares();
		} else if (lastKibEaten == Kibble.NYOOMTIME){
			nyoomTime();
		} else if (lastKibEaten == Kibble.WALLMAGEDDON) {
			wallmageddonResolved = true;
			SnakeGame.wallmageddon();
			wallmageddonResolved = false;
		} else if (lastKibEaten == Kibble.NEGATIVESCORE){
			SnakeGame.negativeScore();
		}
	}

	// makes the square size shrink down so one more square fits both x and y ways.
	private static void shrinkSquares(){
		SnakeGame.squareSize = 500 / (SnakeGame.xSquares + 1);
		SnakeGame.recalculateSquares();
	}

	// shrinks the time between ticks, cancels the old timer and starts a new one. Still a little buggy.
	private static void nyoomTime(){
		if (SnakeGame.clockInterval > 101){
			SnakeGame.clockInterval -= (100);
		}
		SnakeGame.setGameStage(SnakeGame.CANCEL_TIMER);
		SnakeGame.speedUpTimer();
	}

	// changes the kibble type.
	public void changeKibbleType(){
		int kibType = rand.nextInt(8);
		this.kibbleType = kibType;
	}

	public boolean isThisInKibble(Square testSquare) {
		if (kibbleX == testSquare.x && kibbleY == testSquare.y) {
			return true;
		}
		return false;
	}

	public int getLastKibEaten() {
		return lastKibEaten;
	}

	public int getLastColorEaten() {
		return lastColorEaten;
	}

	public void setLastKibEaten(int lastKibEaten) {
		this.lastKibEaten = lastKibEaten;
	}

	public void setLastColorEaten(int lastColorEaten) {
		this.lastColorEaten = lastColorEaten;
	}

	public int getKibbleType() {
		return kibbleType;
	}

	public int getKibbleX() { return kibbleX; }

	public int getKibbleY() { return kibbleY; }

	public void setFirstRound(boolean firstRound) {
		this.firstRound = firstRound;
	}

	public Square getSquare() { return new Square(kibbleX, kibbleY); }


}
