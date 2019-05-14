package com.clara;

import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;

import javax.swing.*;


public class SnakeGame {

	public final static int xPixelMaxDimension = 501;  //Pixels in window. 501 to have 50-pixel squares plus 1 to draw a border on last square
	public final static int yPixelMaxDimension = 501;

	// the 501 pixel window includes the title bar, which I want to include. These are the adjustments to make the game still the right size with having a title bar.  Still needs testing on other computer.
	public final static int xExtraPixelsToAccountForTitleBar = 16;
	public final static int yExtraPixelsToAccountForTitleBar = 39;

	public static int xSquares;    //How many squares in the grid?
	public static int ySquares;

	public static double squareSize = 50.0; // changed to a double because of the shrink square kibble effect, which makes the grid column and row larger.

	protected static Snake snake ;

	private static GameComponentManager componentManager;

	protected static Score score;

	protected static LinkedList<Wall> wallList = new LinkedList<>();

	public static boolean warpWalls = true;

	static final int EASY = 3;
	static final int MID = 8;
	static final int HARD = 15;

	static int gameDifficulty = MID;

	static final int BEFORE_GAME = 1;
	static final int DURING_GAME = 2;
	static final int GAME_OVER = 3;
	static final int GAME_WON = 4;
	static final int OPTIONS = 5;
	static final int CANCEL_TIMER = 6;//The numerical values of these variables are not important. The important thing is to use the constants
	//instead of the values so you are clear what you are setting. Easy to forget what number is Game over vs. game won
	//Using constant names instead makes it easier to keep it straight. Refer to these variables 
	//using statements such as SnakeGame.GAME_OVER 

	private static int gameStage = BEFORE_GAME;  //use this to figure out what should be happening. 
	//Other classes like Snake and DrawSnakeGamePanel will query this, and change its value

	protected static long clockInterval = 500; //controls game speed
	//Every time the clock ticks, the snake moves
	//This is the time between clock ticks, in milliseconds
	//1000 milliseconds = 1 second.

	static JFrame snakeFrame;
	static DrawSnakeGamePanel snakePanel;
	//Framework for this class adapted from the Java Swing Tutorial, FrameDemo and Custom Painting Demo. You should find them useful too.
	//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/FrameDemoProject/src/components/FrameDemo.java
	//http://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html


	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initializeGame();
				createAndShowGUI();
			}
		});
	}


	private static void createAndShowGUI() {
		//Create and set up the window.
		snakeFrame = new JFrame();
		snakeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		snakeFrame.setTitle("Snake Game v 2.0");

		snakeFrame.setSize(xPixelMaxDimension + xExtraPixelsToAccountForTitleBar, yPixelMaxDimension + yExtraPixelsToAccountForTitleBar);
		snakeFrame.setUndecorated(false); // doesn't hide the title bar.
		snakeFrame.setVisible(true);
		snakeFrame.setResizable(false);
		setIcon();

		snakePanel = new DrawSnakeGamePanel(componentManager);

		snakePanel.setFocusable(true);
		snakePanel.requestFocusInWindow(); //required to give this component the focus so it can generate KeyEvents

		snakeFrame.add(snakePanel);

		//Add listeners to listen for key presses
		snakePanel.addKeyListener(new GameControls());
		snakePanel.addKeyListener(new SnakeControls(snake));

		setGameStage(BEFORE_GAME);

		snakeFrame.setVisible(true);
	}

	private static void setIcon(){
		// displays the icon
		String iconURL = "resources/icon.png";
		ImageIcon icon = new ImageIcon(iconURL);
		snakeFrame.setIconImage(icon.getImage());
	}

	private static void initializeGame() {
		//set up score, snake, walls, and first kibble
		xSquares = (int) (xPixelMaxDimension / squareSize);
		ySquares = (int) (yPixelMaxDimension / squareSize);

		componentManager = new GameComponentManager();
		snake = new Snake(xSquares, ySquares);
		Kibble kibble = new Kibble();

		addWalls();
		componentManager.addSnake(snake);
		componentManager.addKibble(kibble);

		score = new Score();
		initializeDatabase();

		componentManager.addScore(score);

		kibble.setLastKibEaten(Kibble.NORMAL);
		kibble.setLastColorEaten(Kibble.NORMAL);

		gameStage = BEFORE_GAME;
	}

	// deletes any existing walls and makes a new set of walls based on the game difficulty.
	protected static void addWalls(){
		if (!wallList.isEmpty()) {
			wallList.clear();
		}
		for (int x = 1; x <= gameDifficulty; x++){
			Wall newWall = new Wall();
			wallList.add(newWall);
		}
	}

	// the troublesome timer.
	protected static Runnable newGame() {
		Timer timer = new Timer();
		SnakeGame.clockInterval = 500;
		gameStage = DURING_GAME;
		GameClock clockTick = new GameClock(componentManager, snakePanel);
		timer.scheduleAtFixedRate(clockTick, 500, clockInterval);
		componentManager.newGame();
		return null;
	}

	// the troublesome timer: I have no one to blame but myself edition
	protected static void speedUpTimer(){
		Timer fastTimer = new Timer();
		gameStage = DURING_GAME;
		GameClock fastTick = new GameClock(componentManager, snakePanel);
		fastTimer.scheduleAtFixedRate(fastTick, 500, clockInterval);
		GameComponentManager.lingeringTimer = true;
	}

	// the options menu timer, slightly less troublesome then the other two timers but you still shouldn't turn you back to it.
	protected static void optionsMenuTimer(){
		Timer optionsTimer = new Timer();
		gameStage = OPTIONS;
		GameClock clockTick = new GameClock(componentManager, snakePanel);
		optionsTimer.scheduleAtFixedRate(clockTick, 0, clockInterval);
	}

	// sets up the high score database
	protected static void initializeDatabase(){
		HighScoreDatabase.createTables();
		if(!HighScoreDatabase.checkForData()){
			HighScoreDatabase.insertStartingData();
		}
	}

	// recalculates the square size based on modified squaresize.
	public static void recalculateSquares(){
		xSquares = (int) (xPixelMaxDimension / squareSize);
		ySquares = (int) (yPixelMaxDimension / squareSize);
	}

	// the wallmageddon effect. called upon by the resolvekibble method. moves all walls, makes sure they don't spawn in the snake and the new kibble stays out of them.
	// the iskibbleoutofwall method is where the resolvekibble method is located, so care has to be taken to avoid a reclusive hole.
	protected static void wallmageddon() {
		for(Wall wall : wallList){
			wall.moveWall();
			componentManager.isWallOutOfSnake();
			componentManager.isKibbleOutOfWall();
		}
	}

	// the bad kibble effect. score set to -2 so that the +1 earned by eating a kibble is canceled out.
	protected static void negativeScore(){
		score.score -= 2;
	}

	public static int getGameStage() {
		return gameStage;
	}

	public static void setGameStage(int gameStage) {
		SnakeGame.gameStage = gameStage;
	}
}
