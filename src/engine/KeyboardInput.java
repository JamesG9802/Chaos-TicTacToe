//Mar 16 2021
package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import window.Window;
/*
 * This class manages keyboard input
 */
public class KeyboardInput implements KeyListener 
{	
	private boolean keysPress[] = new boolean[256];
	private boolean keysLast[] = new boolean[256];
	public KeyboardInput(Window window)
	{
		window.addKeyListener(this);
	}
	public void update()
	{
		keysLast = Arrays.copyOf(keysPress, 256);	//Inputs on current frame are stored 
	}
	public boolean isTapped(char key)
	{
		Character.toUpperCase(key); 
		int temp = (int) key; //Turns to ASCII value 
		if(keysPress[KeyEvent.getExtendedKeyCodeForChar(temp)])
			return true;
		return false;
	}
	public boolean isPressed(char key)
	{
		Character.toUpperCase(key);
		int temp = KeyEvent.getExtendedKeyCodeForChar((int)key);
		if(keysPress[temp] && (keysPress[temp] == keysLast[temp]))	//if the key is pressed and the key was pressed on the previous frame 
																	//and the current frame
			return true;
		return false;
	}
	public boolean isPressed(int key)
	{
		if(keysPress[key] && (keysPress[key] == keysLast[key]))	//if the key is pressed and the key was pressed on the previous frame 
																//and the current frame
			return true;
		return false;
	}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e)	//if the key is pressed it will make the value at the specific index true
	{
		keysPress[e.getKeyCode()] = true;
	}
	public void keyReleased(KeyEvent e) //if the key is released it will make the value at the specific index false
	{
		keysPress[e.getKeyCode()] = false;
	}
}
