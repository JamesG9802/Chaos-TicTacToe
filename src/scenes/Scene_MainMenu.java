//Mar 15 2021
package scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import engine.Engine;
import engine.MouseInput;
import window.Drawable;
import window.Screen;

/*
 * This is the MainMenu scene where the user can exit or play the game.
 */
public class Scene_MainMenu extends Scene{
	public void screenInit()	//Initialize the Screen
	{
		screen = new Screen(new Drawable()
		{
			Dimension screenSize = Engine.engine.window.getSize();	//Screen Size
			//Variables for Objects
			
			/*	objectHovered
			 * 	An array of booleans to determine if the mouse is over a specific button
			 * 	0 = Title
			 * 	1 = Play
			 * 	2 = Exit
			*/
			boolean objectHovered[] = new boolean[3]; 
		
			/*	objectSpecs
			 * 	A 2D array to store each button's x, y, width, and height
			 * 	The first dimension stores the button and the second dimension stores its x,y, width, and height.
			 * 	0 0 = Title.x
			 * 	0 1 = Title.y
			 *  0 2 = Title.width
			 *  0 3 = Title.height
			 */
			int objectSpecs[][] = {
					{(int)(screenSize.width*.1),(int)(screenSize.height*.1),(int)(screenSize.width*.8),(int)(screenSize.height*.1)},	//Title
					{(int)(screenSize.width*.1),(int)(screenSize.height*.8),(int)(screenSize.width*.3),(int)(screenSize.height*.1)},	//Play
					{(int)(screenSize.width*.6),(int)(screenSize.height*.8),(int)(screenSize.width*.3),(int)(screenSize.height*.1)}		//Exit
				};
			
			/*	objectNames
			 * 	An array to store the names of the buttons
			 */
			String objectNames[] = {
					"Chaos Tic-Tac-Toe",
					"Play",
					"Exit"
			};
			//Methods
			@Override
			public void update() {
				screenSize = Engine.engine.window.getSize();	//Reupdates the screenSize incase the window changes
				boundsCalc();
				clickFunctions();
			}
			@Override
			public void draw(Graphics g) {	//Draw Main Menu 
				//Background
				g.setColor(Scene.backgroundColor);
				g.fillRect(-10,-10,screenSize.width+10,screenSize.height+10);
				g.setColor(new Color(0,0,0));
				drawObjects(g);
			}
			//Assistant Methods
			private void boundsCalc()	//Updates the objectHovered array by checking if the mouse is in bounds of an object
			{
				int mouseX = Engine.engine.mouseX;
				int mouseY = Engine.engine.mouseY;
				
				for(int i = 0; i< objectHovered.length;i++)	//For loop to determine if the mouse is in bounds of an object
				{
					objectHovered[i] = inBounds(mouseX,mouseY,objectSpecs[i]);
				}
			}
			private void clickFunctions()	//Determines the various responses when a button is clicked
			{
				for(int i = 0; i< objectHovered.length;i++)
				{
					if(objectHovered[i] && Engine.engine.input1.isReleased(MouseInput.mouse1)) //If left mouse button is clicked
					{
						switch(i)	//Performs different actions depending on which button was pressed
						{
							case 1:
							{
								SceneManager.setScene(SceneKey.Play);
								break;
							}
							case 2:
							{
								System.exit(1);
								break;
							}
						}
					}
				}
			}
			private boolean inBounds(int x, int y, int objectSpecs[])	//given a mouse x and y, this function returns true if the mouse is in bounds
			{
				return (x > objectSpecs[0] &&	//object x
						x < objectSpecs[0] + objectSpecs[2] &&	//object width
						y > objectSpecs[1] &&	//object y
						y < objectSpecs[1] + objectSpecs[3]);	//object height
			}
			private void drawObjects(Graphics g)	//Draws the objects and text to the screen
			{
				//Title Text
				Font titleFont = Scene.defaultFont.deriveFont(64.0f).deriveFont(Font.BOLD);
				g.setColor(new Color(0,0,0));
				for(int i=0; i<objectHovered.length;i++)
				{
					if(i ==0)	//Specifically, only the title has the largest font size and no border
					{
						titleFont = titleFont.deriveFont(64.0f);
					}
					else	//For every other button
					{
						titleFont = titleFont.deriveFont(48.0f);
						if(objectHovered[i])	//Changes color if the mouse is hovering
						{
							Color oldColor = g.getColor();
							g.setColor(Scene.hoverColor);
							g.fillRect(objectSpecs[i][0], objectSpecs[i][1], objectSpecs[i][2], objectSpecs[i][3]);
							g.setColor(oldColor);
						}
						else
							g.drawRect(objectSpecs[i][0], objectSpecs[i][1], objectSpecs[i][2], objectSpecs[i][3]);
					}
					//Draw Object
					this.drawString_Centered(g,
							objectNames[i],
							objectSpecs[i][0], 
							objectSpecs[i][1],
							objectSpecs[i][2],
							objectSpecs[i][3],
							titleFont);
				}
			}
			private void drawString_Centered(Graphics g, String text,int x, int y, int width, int height, Font font) //Draws String centered
			{
				FontMetrics fm = g.getFontMetrics(font);
			    g.setFont(font);
			    int newx = x + (width - fm.stringWidth(text)) / 2;
			    int newy = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
			    g.drawString(text, newx, newy);
			}
		});

	}
}
