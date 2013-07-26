import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;


public class TextButton extends Rectangle{

	//FIELDS
	private String text;
	
	
	//CONSTRUCTOR
	public TextButton(String t, int x, int y, int w, int h){
		text = t;
		setBounds(x, y, w, h);
	}
	
	public String getText(){
		return text;
	}
	
	//GRAPHICS
	public void paint(Graphics g){
		g.drawString(text, x, (y+height));
		//g.fillRect(x, y, width, height);
	}
}
