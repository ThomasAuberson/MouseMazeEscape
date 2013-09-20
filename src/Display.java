import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

/*
 * Java Maze Escape
 * 
 * Author: Thomas Auberson
 * Version: 0.3c
 * 
 * This class controls a JFrame window with a single JPanel canvas display
 */

public class Display extends JPanel implements Runnable {

	// FIELDS
	private String version = "0.3c";
	private JFrame frame;
	private MenuBar menu;
	private ArrayList<Score> scores = new ArrayList<Score>();

	private Dimension SIZE = new Dimension(800, 800);
	private boolean RESIZABLE = false;
	private String TITLE = "Mouse Maze Escape";

	private boolean onLoadScreen = true;
	private boolean onMenuScreen = false;
	private boolean inSandBoxMode = false;
	private boolean inChallengeMode = false;
	private boolean paused = false;

	// Maze
	private MazeGenerator mazeGenerator;
	private Maze maze;
	private int nextMazeSize = 15; // 2 less than level 1 size
	private Character character;
	private int tileSize = 80;

	// HUD
	// private Image jumpImg;
	// private Image lifeImg;
	// private int ICON_SIZE = 25;
	private int time = 0;
	private int seconds = 0;
	private int START_TIME = 30; // 10 less than level 1 time
	private int TIME_INCREASE = 5;
	private int TIME_INCREASE_CHALLENGE = 10;
	private int TIMER_X = 675;
	private int LEVEL_X = 50;
	private int Y_GAP = 25;
	private int score = 0;
	private int exp, EXP_THRESHOLD = 1000; // score but spent automatically on
											// new jump
	private int lives, START_LIVES = 0;
	private int jump, START_JUMPS = 1;
	private int currentLevel;

	// Menu Screen
	private int NUM_MENU_BUTTONS = 4;
	private String[] buttonNames = { "Start Game", "High Scores",
			"How to Play", "About" };
	private TextButton[] menuButtons = new TextButton[NUM_MENU_BUTTONS];
	private int BUTTON_WIDTH = 180, BUTTON_HEIGHT = 35, BUTTON_GAP = 35,
			BUTTON_Y = 340;
	private Font BUTTON_FONT = new Font("AGENCY FB", Font.BOLD, 40);
	private int TITLE_X = 50, TITLE_Y = 160;
	private Font TITLE_FONT = new Font("AGENCY FB", Font.BOLD, 100);
	private int IMAGE_X = 50, IMAGE_Y = 300, IMAGE_W = 300, IMAGE_H = 300;
	private Image mouseImg;
	private int BOX_X = 70, BOX_Y = 230, BOX_W = 660, BOX_H = 470;

	// DIRECTIONS
	private Direction[] directions = { new Direction("NORTH"),
			new Direction("EAST"), new Direction("SOUTH"),
			new Direction("WEST") };

	// CONSTRUCTOR
	public Display() {
		// Initialise the JFrame
		frame = createFrame();
		frame.add(this);
		frame.setVisible(true);

		// Initialise Menu Screen
		for (int i = 0; i < NUM_MENU_BUTTONS; i++) {
			menuButtons[i] = new TextButton(buttonNames[i],
					(this.getWidth()) / 2, BUTTON_Y
							+ (BUTTON_HEIGHT + BUTTON_GAP) * i, BUTTON_WIDTH,
					BUTTON_HEIGHT);
		}
		mouseImg = new ImageIcon("tex/mouse.png").getImage();
		// jumpImg = new ImageIcon("tex/jump.png").getImage();
		// lifeImg = new ImageIcon("tex/life.png").getImage();

		// Generate a menu bar
		menu = new MenuBar(this);
		frame.setJMenuBar(menu);

		// Initialise Mouse Listeners
		MouseHandler mouse = new MouseHandler(this);
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);

		// Initialise Key Listener
		KeyHandler key = new KeyHandler(this);
		this.addKeyListener(key);
		requestFocusInWindow();

		// Initialise MazeGenerator
		mazeGenerator = new MazeGenerator();

		// Initialise Character
		Point p = new Point((this.getWidth() - tileSize) / 2,
				(this.getHeight() - tileSize) / 2);
		character = new Character(p, tileSize);
		loadScores();

		// Initialise Thread
		Thread thread = new Thread(this);
		thread.start();
		onLoadScreen = false;
		onMenuScreen = true;
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SIZE);
		frame.setTitle(TITLE);
		frame.setResizable(RESIZABLE);
		frame.setLocationRelativeTo(null); // Sets window in centre
		frame.setLayout(new GridLayout(1, 1, 0, 0));
		return frame;
	}

	public void quitToMenu() {
		paused = true;
		int n = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to quit to main menu?", "Game Paused",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			endGame();
		}
		paused = false;
	}

	public void addHighScore(int s, String n) {
		for (int i = 0; i < scores.size(); i++) {
			if (s > scores.get(i).getScore()) {
				scores.add(i, new Score(s, n));
				return;
			}
		}
		scores.add(new Score(s, n));
		scores.remove(5);
	}

	public void loadScores() {
		try {
			Scanner scan = new Scanner(new File("scores.sav"));
			while (scan.hasNext()) {
				int score = scan.nextInt();
				String name = scan.nextLine();
				scores.add(new Score(score, name));
			}
			scan.close();
		} catch (Exception e) {
		}

	}

	public void startGame() {
		currentLevel = 1;
		lives = START_LIVES;
		jump = START_JUMPS;
		score = 0;
		exp = 0;
		startLevel();
	}

	public void startLevel() {
		maze = mazeGenerator.generateMaze(new Dimension(nextMazeSize,
				nextMazeSize), tileSize);
		character.startLevel(maze, directions[2]);
		time = 0;
		seconds = START_TIME + (TIME_INCREASE * (currentLevel - 1));
	}

	public void endGame() {
		String s = JOptionPane.showInputDialog(this,
				"<html>Game Over!<br>Your final score was: " + score
						+ "<br>Type a name to submit your score:</html>",
				"Game Over", JOptionPane.PLAIN_MESSAGE);
		if (s != null && !s.equals("")) {
			addHighScore(score, s);
			try {
				PrintStream prin = new PrintStream("scores.sav");
				for (int i = 0; i < scores.size(); i++) {
					prin.println(scores.get(i).getScore() + " "
							+ scores.get(i).getName());
				}
				prin.close();
			} catch (Exception e) {
			}
		}
		maze = null;
		onMenuScreen = true;
	}

	// MOUSE ACTION LISTENR
	public void mousePressed(Point p, int button) {
		SwingUtilities.convertPointFromScreen(p, this);
		requestFocusInWindow();
		System.out.println("Mouse " + p);
		if (onMenuScreen && (button == 1)) {
			for (int i = 0; i < NUM_MENU_BUTTONS; i++) {
				if (menuButtons[i].contains(p)) {
					System.out.println(menuButtons[i].getText());
					menuButtonClicked(menuButtons[i].getText());
					return;
				}
			}
		}
	}

	// KEY LISTENER
	public void keyPressed(int code) {
		if (onMenuScreen)
			return;
		else {
			if (code >= 37 && code <= 40) { // ARROW KEYS
				int dn = code - 38;
				if (dn == -1)
					dn = 3;
				character.startMoving(directions[dn]);
			}
			if (code == 32) { // SPACEBAR
				if (jump > 0) {
					character.jump();
					jump--;
				}
			} else if (code == 27) { // "ESC"
				quitToMenu();
			}
		}
	}

	public void keyReleased(int code) {
		if (onMenuScreen)
			return;
		if (code >= 37 && code <= 40) { // Arrow Keys
			int dn = code - 38;
			if (dn == -1)
				dn = 3;
			character.stopMoving(directions[dn]);
		}
	}

	// MENU ACTION LISTENER
	public void menuButtonClicked(String button) {
		if (button.equals("Start Game")) {
			onMenuScreen = false;
			startGame();
		} else if (button.equals("About")) {
			JOptionPane
					.showMessageDialog(
							this,
							"<HTML>Mouse Maze Escape<br>Version: "
									+ version
									+ "<br>Author: Thomas Auberson<br><br>You are a MOUSE!<br>You are in a MAZE!!<br>You must ESCAPE!!!</HTML>",
							"About", JOptionPane.PLAIN_MESSAGE);
		} else if (button.equals("High Scores")) {
			String scoreString = "";
			int index = 1;
			
			
			for (int i = 0; i<scores.size(); i++) {
				scoreString = scoreString + "<br>" + index + ") "
						+ scores.get(i).getScore() + " - " + scores.get(i).getName();
				index++;
			}
			JOptionPane.showMessageDialog(this, "<HTML>High Scores:"
					+ scoreString + "</HTML>", "High Scores",
					JOptionPane.PLAIN_MESSAGE);
		} else if (button.equals("How to Play")) {
			JOptionPane
					.showMessageDialog(
							this,
							"<HTML>- You must reach the end of each maze, (represented<br>  by a red tile) before the time runs out to<br>  progress to the next level.<br>- The mazes get bigger (and ergo harder) with each level!<br>- Use the arrow keys UP, DOWN, LEFT, RIGHT to<br>  manuevre through the maze.<br>- Use SPACE to jump over walls! This uses jump points.<br>  You earn additional jump points as your score increases.<br>- Use ESC to pause the game or quit back to the main menu.</HTML>",
							"How to Play", JOptionPane.PLAIN_MESSAGE);
		}
	}

	// GRAPHICS
	public void paintComponent(Graphics g) {
		g.setColor(Color.black); // g.setColor(new Color(10, 10, 50)); //
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (onLoadScreen) {
			g.setFont(BUTTON_FONT);
			g.setColor(Color.white);
			g.drawString("LOADING", 340, 390);
			return;
		}
		// Paint Menu Screen
		if (onMenuScreen) {
			g.setColor(Color.white);
			g.fillRect(BOX_X, BOX_Y, BOX_W, BOX_H);
			g.setFont(TITLE_FONT);
			g.drawString(TITLE, TITLE_X, TITLE_Y);

			g.setColor(Color.black);
			g.setFont(BUTTON_FONT);
			for (int i = 0; i < NUM_MENU_BUTTONS; i++) {
				menuButtons[i].paint(g);
			}

			g.drawImage(mouseImg, IMAGE_X, IMAGE_Y, IMAGE_W, IMAGE_H, null);

		} else {
			// Paint Maze
			maze.paint(g);

			// Paint Character
			character.paint(g);

			// Paint HUD
			g.setColor(Color.red);
			g.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
			g.drawString("Timer: " + seconds, TIMER_X, Y_GAP);
			g.drawString("Level: " + currentLevel, LEVEL_X, Y_GAP);
			g.drawString("Score: " + score, LEVEL_X, Y_GAP * 2);
			//g.drawString("Lives: " + lives, TIMER_X, 2 * Y_GAP);
			g.drawString("Jumps: " + jump, TIMER_X, Y_GAP * 2);
		}
	}

	// THREAD
	public void run() {
		while (true) {

			repaint();

			while (paused) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			while (exp > EXP_THRESHOLD) {
				exp -= EXP_THRESHOLD;
				jump++;
			}

			if (!onMenuScreen && !onLoadScreen)
				time++;
			if (time >= 100) {
				time = 0;
				seconds--;
			}

			if ((seconds < 0) && (maze != null)) {
				seconds = 0;
				if (lives >= 1) {
					lives--;
					JOptionPane.showMessageDialog(this,
							"<html>You have run out of time!<br>You now have "
									+ lives + " lives left.", "Level Failed",
							JOptionPane.PLAIN_MESSAGE);
					startLevel();

				} else {
					endGame();
				}
			}

			if (character.isDead) {
				character.isDead = false;
				seconds = 0;
				if (lives >= 1) {
					lives--;
					JOptionPane.showMessageDialog(this,
							"<html>You landed on the wall!<br>You now have "
									+ lives + " lives left.", "Level Failed",
							JOptionPane.PLAIN_MESSAGE);
					startLevel();

				} else {
					endGame();
				}
			}

			if (character.isAtEnd) {
				int timeBonus = seconds;
				score += (currentLevel * (50 + timeBonus) + 50);
				exp += (currentLevel * (50 + timeBonus) + 50);
				JOptionPane.showMessageDialog(this,
						"<html>You have completed level " + currentLevel
								+ "!<br>You earned a time bonus of: "
								+ timeBonus * currentLevel
								+ "<br>Your current score is: " + score
								+ "</html>", "Level Complete",
						JOptionPane.PLAIN_MESSAGE);

				if ((currentLevel % 5) == 0) {
					JOptionPane
							.showMessageDialog(
									this,
									"<html>Congratulations on making it past level "
											+ currentLevel
											+ "<br>You have earned an additional life!</html>",
									"Gained a Life", JOptionPane.PLAIN_MESSAGE);
					//lives++;
				}
				character.isAtEnd = false;
				currentLevel++;
				nextMazeSize += 2;
				startLevel();

			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void main(String[] args) {
		new Display();
	}
}
