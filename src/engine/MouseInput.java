// Mar 16 2021
package engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import window.Window;
/*
 * This class controls the mouse inputs
 */
public class MouseInput implements MouseListener
{
	public static final int mouse1 = MouseEvent.BUTTON1; //Left Mouse Button
	private final int numOfButtons = java.awt.MouseInfo.getNumberOfButtons();
	private boolean buttonsPress[] = new boolean[numOfButtons];
	private boolean buttonsLast[] = new boolean[numOfButtons];
	public MouseInput(Window window)
	{
		window.addMouseListener(this);
	}
	public void update()
	{
		buttonsLast = Arrays.copyOf(buttonsPress,numOfButtons);
	}
	public boolean isReleased(int button)
	{
		if(buttonsLast[button]&&!buttonsPress[button])
		//if the key is released
			return true;
		return false;
	}
	public boolean isPressed(int button)
	{
		if(buttonsPress[button] && buttonsLast[button])	
		//if the key is pressed and the key was pressed on the previous frame and the current frame
			return true;
		return false;
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		buttonsPress[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e) {
		buttonsPress[e.getButton()] = false;
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}