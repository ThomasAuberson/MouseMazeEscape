import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

public class Character {

	// FIELDS
	private Image[] run = new Image[4];
	private Image[] jump = new Image[4];
	private Maze maze;
	private int tileSize;

	private Point coords;
	private Point screenCoords;
	private Direction facing;

	private int speed = 4;
	private int jumpSpeed = 8;
	private int jumpIndex = 0;

	private boolean isJumping = false;
	private boolean isMoving = false;
	private boolean isStopping = false;
	private boolean isTurning = false;
	private Direction nextFacing;
	public boolean isAtEnd = false;
	public boolean isDead = false;

	// CONSTRUCTOR
	public Character(Point sc, int ts) {
		for (int i = 0; i < 4; i++) {
			run[i] = new ImageIcon("tex/mouse_" + i + ".png").getImage();
			jump[i] = new ImageIcon("tex/mouse_jump_" + i + ".png").getImage();
		}
		screenCoords = sc;
		tileSize = ts;
		// setBounds(sc.x, sc.y, ts, ts);
	}

	public void startLevel(Maze m, Direction f) {
		isMoving = false;
		facing = f;
		maze = m;
		coords = maze.getStart();
		maze.setFocus(new Point(screenCoords.x - coords.x, screenCoords.y
				- coords.y));
	}

	// ACTIONS
	public void action() {
		if (coords.equals(maze.getEnd())) {
			isAtEnd = true;
			isMoving = false;
		} else if (isMoving) {
			move(facing, false);
			if (isTurning) {
				if (((coords.x % tileSize) == 0)
						&& ((coords.y % tileSize) == 0)) {
					isTurning = false;
					facing = nextFacing;
				}
			} else if (isStopping) {
				if (((coords.x % tileSize) == 0)
						&& ((coords.y % tileSize) == 0)) {
					isStopping = false;
					isMoving = false;
				}
			}
		} else if (isJumping) {
			jumpMove(facing);
		}
	}

	public void jumpMove(Direction d) {
		System.out.println("Jump Part 1");
		if (jumpIndex == (2 * tileSize / jumpSpeed)) {
			isJumping = false;
			jumpIndex = 0;
			if (!maze.tileTraversible(new Point((coords.x / tileSize),
					(coords.y / tileSize)))) {
				isDead = true;
			}
		} else {
			coords = d.next(coords, jumpSpeed);
			maze.setFocus(new Point(screenCoords.x - coords.x, screenCoords.y
					- coords.y));
			jumpIndex++;
			System.out.println("Jump Part 2");
		}
	}

	public void move(Direction d, boolean recurred) {
		boolean blocked = false;
		if ((d.getName().equals("NORTH")) || (d.getName().equals("SOUTH"))) {
			if ((coords.x % tileSize) != 0)
				blocked = true;
			if ((coords.y % tileSize) == 0) {
				Point newTile = d.next(new Point(
						(coords.x - (coords.x % tileSize)) / tileSize,
						(coords.y - (coords.y % tileSize)) / tileSize), 1);
				if (!maze.tileTraversible(newTile))
					blocked = true;
			}
		}
		if ((d.getName().equals("WEST")) || (d.getName().equals("EAST"))) {
			if ((coords.y % tileSize) != 0)
				blocked = true;
			if ((coords.x % tileSize) == 0) {
				Point newTile = d.next(new Point(
						(int) ((coords.x - (coords.x % tileSize)) / tileSize),
						(coords.y - (coords.y % tileSize)) / tileSize), 1);
				if (!maze.tileTraversible(newTile))
					blocked = true;
			}
		}
		if (!blocked) {
			coords = d.next(coords, speed);
			maze.setFocus(new Point(screenCoords.x - coords.x, screenCoords.y
					- coords.y));
		}
	}

	public void startMoving(Direction d) {
		if (isMoving) {
			isTurning = true;
			nextFacing = d;
		} else {
			isMoving = true;
			facing = d;
		}
	}

	public void stopMoving(Direction d) {
		if (facing == d) {
			isStopping = true;
		}
	}

	public void jump() {
		isStopping = true;
		isJumping = true;
		System.out.println("Coords: " + screenCoords);
	}

	// GRAPHICS
	public void paint(Graphics g) {
		action();
		if (isJumping)
			g.drawImage(jump[facing.index()], screenCoords.x, screenCoords.y,
					tileSize, tileSize, null);
		else {
			g.drawImage(run[facing.index()], screenCoords.x, screenCoords.y,
					tileSize, tileSize, null);
		}
	}
}
