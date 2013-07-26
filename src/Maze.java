import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class Maze {

	// FIELDS
	Character character;

	// MAZE MAP (TILES)
	private int[][] mazeMap;
	private Dimension dimension; // Dimensions - SHOULD BE ODD!
	private Point start, end;

	// DISPLAY
	private TileSet t; // Tileset library
	private int tileSize; // Size of tiles in pixels
	private Point focus; // Screen coord NOT tile coord

	// CONSTRUCTOR
	public Maze(int[][] m, Point s, Point e, int ts, Dimension d,
			TileSet tileSet) {
		dimension = d;
		tileSize = ts;
		t = tileSet;
		mazeMap = m;
		start = s;
		end = e;
		focus = new Point((int) ((start.x + 0.5) * tileSize),
				(int) ((start.y + 0.5) * tileSize));
	}

	public void setFocus(Point coords) {
		focus = coords;
	}

	public boolean tileTraversible(Point p) {
		if (p.x < 0 || p.x >= dimension.width || p.y < 0
				|| p.y >= dimension.height)
			return false;
		if (mazeMap[p.x][p.y] == t.WALL)
			return false;
		return true;
	}

	// GETTERS & SETTERS
	public Point getStart() {
		return new Point(start.x * tileSize, start.y * tileSize);
	}

	public Point getEnd() {
		return new Point(end.x * tileSize, end.y * tileSize);
	}

	// GRAPHICS
	public void paint(Graphics g) {
		// System.out.println("Focus "+focus);
		for (int x = 0; x < dimension.width; x++) {
			for (int y = 0; y < dimension.height; y++) {
				// System.out.println("Paint tile: "+x+" "+y);
				t.paint(g, x * tileSize + focus.x, y * tileSize + focus.y,
						tileSize, mazeMap[x][y]);
			}
		}
	}
}
