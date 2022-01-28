//Jan 26 2021
package window;

import java.awt.Graphics;
/*
 * This interface stores the format for drawing to the screen.
 */
public interface Drawable {
	
	public void update();	//Updates the program
	public void draw(Graphics g);	//Renders iamges to the screen
}
