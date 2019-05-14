package com.clara;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

/** This class responsible for displaying the graphics, so the snake, grid, kibble, instruction text and high score
 * 
 * @author Clara
 *
 */
public class DrawSnakeGamePanel extends JPanel {

    private static int gameStage = SnakeGame.BEFORE_GAME;  //use this to figure out what to paint

    private Snake snake;
    private Kibble kibble;
    private Score score;
    private Wall wall;

    private JTextField playerNameTextField = new JTextField("Enter Name Here");
    private JButton SubmitScoreButton = new JButton("Submit Score");

    private boolean submittedScore = false;


    DrawSnakeGamePanel(GameComponentManager components) {
        this.snake = components.getSnake();
        this.kibble = components.getKibble();
        this.score = components.getScore();
        this.wall = components.getWall();
    }

    public Dimension getPreferredSize() {
        return new Dimension(SnakeGame.xPixelMaxDimension, SnakeGame.yPixelMaxDimension);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* Where are we at in the game? 5 phases..
         * 1. Before game starts
         * 1b. The Options menu
         * 2. During game
         * 3. Game lost aka game over
         * 4. or, game won
         */

        gameStage = SnakeGame.getGameStage();

        switch (gameStage) {
            case SnakeGame.BEFORE_GAME: {
                displayInstructions(g);
                break;
            }
            case SnakeGame.OPTIONS: {
                displayOptions(g);
                break;
            }
            case SnakeGame.DURING_GAME: {
                displayGame(g);
                break;
            }
            case SnakeGame.GAME_OVER: {
                displayGameOver(g);
                listenTime(g);
                break;
            }
            case SnakeGame.GAME_WON: {
                displayGameWon(g);
                break;
            }
        }
    }

    // the UI of the options menu
    private void displayOptions(Graphics g) {
        remove(SubmitScoreButton);
        remove(playerNameTextField);
        g.drawString("Press W to enable or disable warpwalls.", 150, 100);
        g.drawString("Press D to adjust the difficulty.", 150, 125);
        g.drawString("Press esc to go back to the main menu.", 150, 150);
        if (SnakeGame.warpWalls) {
            g.drawString("Warpwalls: ON", 150, 200);
        } else {
            g.drawString("Warpwalls: OFF", 150, 200);
        }

        if (SnakeGame.gameDifficulty == SnakeGame.EASY){
            g.drawString("Difficulty: Easy",150, 225);
        } else if (SnakeGame.gameDifficulty == SnakeGame.MID){
            g.drawString("Difficulty: Medium",150, 225);
        } else{
            g.drawString("Difficulty: Hard",150, 225);
        }
    }

    private void displayGameWon(Graphics g) {
        g.clearRect(100, 100, 350, 350);
        g.drawString("HOLY FLIPPING CRACKERJACKS!!!", 150, 150);
        g.drawString("YOU WON SNAKE!!!", 150, 175);
        g.drawString("I DIDN'T THINK IT COULD BE DONE!!!", 150, 200);
        g.drawString("YOU SURE SHOWED ME!!!", 150, 225);
        g.drawString("YOU BESTED ME AT MY OWN GAME!!!", 150, 250);
        g.drawString("YOU HAVE USURPED ME!!!", 150, 275);
        g.drawString("YOU'RE THE PROGRAMMER NOW!!!", 150, 300);
        g.drawString("GOOD LUCK WITH THOSE BUGS!!!", 150, 325);
        g.drawString("YOU'RE GONNA NEED IT!!!", 150, 350);
        g.drawString("I'LL BE OVER HERE TAKING A NAP!!!", 150, 375);
    }

    private void displayGameOver(Graphics g) {
        g.clearRect(75, 75, 350, 350);
        g.drawString("GAME OVER", 150, 125);

        String textScore = score.getStringScore();
        String textHighScore = Integer.toString(HighScoreDatabase.getHighScore());
        String highPlayer = HighScoreDatabase.getHighPlayer();
        String newHighScore = score.newHighScore();

        g.drawString("SCORE = " + textScore, 150, 175);

        g.drawString("HIGH SCORE = " + highPlayer + ": " + textHighScore, 150, 200);
        g.drawString(newHighScore, 150, 225);

        g.drawString("press enter to play again", 150, 250);
        g.drawString("Press q or esc to quit the game", 150, 275);

        if(submittedScore){
            g.drawString("SCORE SUBMITTED", 150, 300);
        }

        playerNameTextField.setBounds(115,325,200,25);
        add(playerNameTextField);

        SubmitScoreButton.setBounds(150,350,115,25);
        add(SubmitScoreButton);
    }

    // resets the score submission objects and displays the game objects.
    private void displayGame(Graphics g) {
        submittedScore = false;
        remove(SubmitScoreButton);
        remove(playerNameTextField);
        displayGameGrid(g);
        displaySnake(g);
        displayKibble(g);
        for(Wall wall: SnakeGame.wallList) {
            displayWall(g, wall);
        }
    }

    // draws a graph so you don't get lost among the beautiful 90s roller rink carpet that this method is also responsable for producing.
    private void displayGameGrid(Graphics g) {
        int maxX = SnakeGame.xPixelMaxDimension;
        int maxY = SnakeGame.yPixelMaxDimension;
        int squareSize = (int) Math.round(SnakeGame.squareSize);

		String FloorTextureURL = "Resources/Floor.png";
		ImageIcon i1 = new ImageIcon(FloorTextureURL);

		g.clearRect(0, 0, maxX, maxY);

        // a linked list to keep track of all the floor tiles.
        LinkedList<Square> floor = new LinkedList<>();

        // makes a floor tile for every square on the grid.
        for (int x = 0; x <= maxX; x+= squareSize){
			for (int y = 0; y <= maxY; y+= squareSize){
				floor.add(new Square(x,y));
			}
		}

        // textures the floor tiles, or makes them white if for some reason the program cannot find the floor texture.
        for (Square s : floor){
			g.drawImage(i1.getImage(), s.x , s.y , squareSize, squareSize, Color.white,null);
		}

        //Draw grid - horizontal lines
        for (int y = 0; y <= maxY; y += squareSize) {
            g.drawLine(0, y, maxX, y);
        }

        //Draw grid - vertical lines
        for (int x = 0; x <= maxX; x += squareSize) {
            g.drawLine(x, 0, x, maxY);
        }
    }

    private void displayKibble(Graphics g) {

        int size = (int) Math.round(SnakeGame.squareSize);

        int x = kibble.getKibbleX() * size;
        int y = kibble.getKibbleY() * size;

        String KibTextureURL = "Resources/Blank.png";

        // figures out the kibble type and assigns it a color or a texture based on that.
        if (kibble.getKibbleType() == Kibble.GREEN){
            g.setColor(Color.GREEN);
        } else if (kibble.getKibbleType() == Kibble.PINK){
            g.setColor(Color.PINK);
        } else if (kibble.getKibbleType() == Kibble.BLUE){
            g.setColor(Color.BLUE);
        }  else if (kibble.getKibbleType() == Kibble.NYOOMTIME){
            KibTextureURL = "Resources/Nyoom.png";
        }else if (kibble.getKibbleType() == Kibble.NEGATIVESCORE){
            KibTextureURL = "Resources/BadActually.png";
        }else if (kibble.getKibbleType() == Kibble.WALLMAGEDDON){
            KibTextureURL = "Resources/WallSwap.png";
        }else if (kibble.getKibbleType() == Kibble.SHRINKSQARES){
            KibTextureURL = "Resources/ShrinkGame.png";
        }else {
            g.setColor(Color.GRAY);
        }

        g.fillRect(x, y, size, size);
        ImageIcon i1 = new ImageIcon(KibTextureURL);
        g.drawImage(i1.getImage(),x, y, size, size,null);
    }

    // paints the walls.
    private void displayWall(Graphics g, Wall theWall) {
        String WallTextureURL = "Resources/Wall.png";
        ImageIcon i1 = new ImageIcon(WallTextureURL);

        int size = (int) Math.round(SnakeGame.squareSize);

        int wallX = theWall.getWallX() * size;
        int wallY = theWall.getWallY() * size;

        g.drawImage(i1.getImage(),wallX, wallY, size, size,null);
    }

    private void displaySnake(Graphics g) {
        int size = (int) Math.round(SnakeGame.squareSize);

        LinkedList<Square> coordinates = snake.getSnakeSquares();

        //Draw's head texture
        drawSnakeHead(g,coordinates);

        String SkinTextureURL = "Resources/Skin.png";

        // figures out what color the snake should be based on the last time a color kibble was eaten.
        if((kibble.getLastColorEaten() == Kibble.NORMAL)){
            SkinTextureURL = "Resources/Skin.png";
        }else if((kibble.getLastColorEaten() == Kibble.GREEN)){
            SkinTextureURL = "Resources/SkinGreen.png";
        }else if((kibble.getLastColorEaten() == Kibble.PINK)){
            SkinTextureURL = "Resources/SkinPink.png";
        }else if((kibble.getLastColorEaten() == Kibble.BLUE)){
            SkinTextureURL = "Resources/SkinBlue.png";
        }

        ImageIcon i1 = new ImageIcon(SkinTextureURL);

        // paints every square in the snake linked list with the snake texture of choice.
        for (Square s : coordinates) {
            g.drawImage(i1.getImage(),s.x * size, s.y * size, size, size,null);
        }

    }

    private void displayInstructions(Graphics g) {
        g.drawString("Press the enter key to begin!", 150, 200);
        g.drawString("Press o for options!", 150, 250);
        g.drawString("Press q or esc to quit the game!", 150, 300);
    }

    // draws snakes head facing whatever direction the current heading is.
    private void drawSnakeHead(Graphics g, LinkedList<Square> coordinates){
        String HeadTextureURL = "Resources/Head.png";

        // figures out which color the head should be based on the last color kibble eaten.
        if(kibble.getLastColorEaten() == Kibble.GREEN){
            HeadTextureURL = "Resources/HeadGreen.png";
        }else if(kibble.getLastColorEaten() == Kibble.PINK){
            HeadTextureURL = "Resources/HeadPink.png";
        }else if(kibble.getLastColorEaten() == Kibble.BLUE){
            HeadTextureURL = "Resources/HeadBlue.png";
        }

        ImageIcon i2 = new ImageIcon(HeadTextureURL);
        int size = (int) Math.round(SnakeGame.squareSize);
        Graphics2D g2 = (Graphics2D)g;

        // gets the games default transformation so it can be returned to later.
        AffineTransform old = g2.getTransform();
        AffineTransform trans = new AffineTransform();

        Square head = coordinates.pop();

        // figures out how much it needs to rotate the panel to get the snake head to face the direction of your current heading.
        if(snake.getCurrentHeading() == snake.DIRECTION_LEFT){
            trans.rotate( Math.toRadians(0), head.x * size, head.y* size );
            g2.transform( trans );
            g2.drawImage(i2.getImage(),head.x * size, head.y * size, size, size,null);
        } else if(snake.getCurrentHeading() == snake.DIRECTION_UP){
            trans.rotate( Math.toRadians(90), head.x * size, head.y* size );
            g2.transform( trans );
            g2.drawImage(i2.getImage(),head.x * size, (head.y * size) - size, size, size,null);
        } else if(snake.getCurrentHeading() == snake.DIRECTION_RIGHT){
            trans.rotate( Math.toRadians(180), head.x * size, head.y* size );
            g2.transform( trans );
            g2.drawImage(i2.getImage(),(head.x * size) - size, (head.y * size) - size, size, size,null);
        } else {
            trans.rotate( Math.toRadians(270), head.x * size, head.y* size );
            g2.transform( trans );
            g2.drawImage(i2.getImage(),(head.x * size) - size, head.y * size, size, size,null);
        }
        g2.setTransform(old);
	}

	// sets up the action listener for the button.
	private void listenTime(Graphics g){
        SubmitScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = playerNameTextField.getText();
                HighScoreDatabase.addScore(name, score.getScore());
                submittedScore = true;

                // Changes the game stage to and from game over so the game over display is repainted.
                // Necessary because there isn't (or shouldn't be) an active timer to repaint the display and show the submitted score text.
                // and there cannot be an active timer redrawing the panel because if there is it will refresh the panel as you are trying to type your name and make the text field empty again.
                SnakeGame.setGameStage(SnakeGame.CANCEL_TIMER);
                SnakeGame.setGameStage(SnakeGame.GAME_OVER);
            }
        });

    }
}

