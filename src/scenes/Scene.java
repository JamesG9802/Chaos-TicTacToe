// Mar 15 2021
package scenes;

import java.awt.Color;
import java.awt.Font;

import engine.Engine;
import window.Screen;
/*
 * This class is the template for all the major scenes in the application.
 */
public abstract class Scene {
//VARIABLES
	public static final Font defaultFont = new Font("Candara",Font.PLAIN,12);	//Default font
	public static final Color backgroundColor = new Color(146,129,139);
	public static final Color hoverColor = new Color(88,81,87);
	public static final Color iconColor = new Color(112,146,190);
	public static Screen screen;	//The scene in memory that will be loaded to the Engine's screen.

//METHODS
	public abstract void screenInit();	//Initializes Screens
	public void loadScene()	//Sets the static screen into the Engine
	{	
		screenInit();
		Engine.engine.window.setScreen(screen);
	}
}
