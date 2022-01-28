//Mar 16 2021
package scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Engine;
import engine.MouseInput;
import window.Drawable;
import window.Screen;

/* 
 * 
 * This class lets the user select which tic-tac-toe variants are selected.
 */
public class Scene_Play extends Scene{

	@Override
	public void screenInit() {
		screen = new Screen(new Drawable()
		{
			//Variables for Objects
			Dimension screenSize = Engine.engine.window.getSize();	//Screen Size
			int currentIcon = -1;	//The icon that is currently clicked; -1 represents default
			
			/*
			 * desc
			 * A 2D array of Strings storing each Icon's title and description
			 *  0 0 = Ultimate.Title
			 *  0 1 = Ultimate.Description
			 */
			String desc[][] = {
					{"Tic-Tac-Toe","Place an X if you are player 1 or an O if you are player 2 to try and get 3 of your symbol in a row."},
					{"Ultimate Tic-Tac-Toe","The board becomes a 3 x 3 board of smaller tic-tac-toe boards."
						+ "\n\n Player 1 can play a move anywhere."
						+ "\n\n Every subsequent move must be played in the smaller board that corresponds to the previous player's move."
						+ "\n\n\n For example, if Player 1 placed an X in the top-right corner in one of the smaller boards, Player 2 must make a move in the top right board."},
					{"Wild Tic-Tac-Toe","Both players can use either an X or an O.\n (Incompatible with Notakto)"},
					{"Misere Tic-Tac-Toe","If you would win, you lose instead."},
					{"Notakto Tic-Tac-Toe","Both players can only use X.\n (Incompatible with Wild)"},
					{"Random Tic-Tac-Toe","A coin toss determines which player's turn it is."},
			};
			/*
			 * iconToggle
			 * An array of booleans to store the toggle states of each icon
			 * 	0 = Ultimate
			 * 	1 = Wild
			 * 	2 = Misere
			 *  3 = Notakto
			 *  4 = Random
			 */
			boolean iconToggle[] = new boolean[5];
			
			/*	objectHovered
			 * 	An array of booleans to determine if the mouse is over a specific toggle
			 * 	0 = Ultimate
			 * 	1 = Wild
			 * 	2 = Misere
			 *  3 = Notakto
			 *  4 = Random
			 *  5 = Back button
			 *  6 = Start Button
			*/
			boolean objectHovered[] = new boolean[7]; 
		
			/*	objectSpecs
			 * 	A 2D array to store each button's x, y, width, and height
			 * 	The first dimension stores the button and the second dimension stores its x,y, width, and height.
			 * 	0 0 = Title.x
			 * 	0 1 = Title.y
			 *  0 2 = Title.width
			 *  0 3 = Title.height
			 */
			int objectSpecs[][] = {
					{(int)(screenSize.width*.2),(int)(screenSize.height*.05),(int)(screenSize.width*.55),(int)(screenSize.height*.15)},	//Title
					{(int)(screenSize.width*.05),(int)(screenSize.height*.05),(int)(screenSize.width*.1),(int)(screenSize.height*.85)},	//IconTray
					{(int)(screenSize.width*.2),(int)(screenSize.height*.25),(int)(screenSize.width*.55),(int)(screenSize.height*.65)},	//Description
					{(int)(screenSize.width*.8),(int)(screenSize.height*.7),(int)(screenSize.width*.15),(int)(screenSize.height*.15)},	//Back
					{(int)(screenSize.width*.8),(int)(screenSize.height*.5),(int)(screenSize.width*.15),(int)(screenSize.height*.15)}	//Start Button
				};
			
			/*	objectNames
			 * 	An array to store the names of the buttons
			 */
			String objectNames[] = {
					"Tic-Tac-Toe",	//Title
					"",	//IconTray
					"",	//Description
					"Back",
					"Play"
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
				//IconTray is drawn first
				drawIconTray(g);
				//Base Objects are drawn last
				drawObjects(g);
			}
			
			//Assistant Methods
			private void boundsCalc()	//Updates the objectHovered array by checking if the mouse is in bounds of an object
			{
				int mouseX = Engine.engine.mouseX;
				int mouseY = Engine.engine.mouseY;
				
				for(int i = 0; i< objectHovered.length;i++)	//Clears objectHovered 
				{
					objectHovered[i] = false;
				}
				//Determines which icon is selected
				objectHovered[(int) ((mouseY-objectSpecs[1][1])/(objectSpecs[1][3]/5))] = inBounds(mouseX,mouseY,objectSpecs[1]);
				objectHovered[5] = inBounds(mouseX,mouseY,objectSpecs[3]); //Determines if the back button is hovered
				objectHovered[6] = inBounds(mouseX,mouseY,objectSpecs[4]); //Determines if the Start button is hovered	
			}
			private boolean inBounds(int x, int y, int objectSpecs[])	//given a mouse x and y, this function returns true if the mouse is in bounds
			{
				return (x > objectSpecs[0] &&	//object x
						x < objectSpecs[0] + objectSpecs[2] &&	//object width
						y > objectSpecs[1] &&	//object y
						y < objectSpecs[1] + objectSpecs[3]);	//object height
			}
			private void clickFunctions()	//Determines the various responses when a button is clicked
			{
				if(Engine.engine.input1.isReleased(MouseInput.mouse1))	//If left mouse button is clicked
				{
					boolean checkHovered = false; //remains false if no object was hovered when mouse was clicked
					for(int i = 0; i< objectHovered.length;i++)
					{
						if(objectHovered[i]) //Icon is hovered when the mouse was clicked
						{
							if(i == 5)	//Back Button 
							{
								SceneManager.setScene(SceneKey.MainMenu); //Changes scene if the back button is pressed
								break;
							}
							else if(i == 6)	//Start Button
							{
								Scene_Game.rules = iconToggle;
								SceneManager.setScene(SceneKey.Game);
								break;
							}
							else	//Icons
							{
								if(!(i == 1 && iconToggle[3]) && !(i==3 && iconToggle[1]))	//Prevents wild and notakto from being selected simultaneously 
									iconToggle[i] = !iconToggle[i]; //Turns true to false and vice versa for the icons
							}
							currentIcon = i;
							checkHovered = true;
						}
					}
					if(!checkHovered)	//if checkHovered is still false, then no interactive object was clicked
					{
						currentIcon = -1;
					}
				}
			}
			private void drawIconTray(Graphics g)	//Draws the IconTray
			{
				for(int i =0;i<5;i++)	//Draws the boundaries of the icontray
				{
					if(iconToggle[i] || objectHovered[i])	//If the icon is toggled or hovered
					{
						if(objectHovered[i])	//Draws a shaded texture if hovered
						{	
							Color oldColor = g.getColor();
							g.setColor(new Color(92,165,95));
							g.fillRect(objectSpecs[1][0], (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0)), objectSpecs[1][2], objectSpecs[1][3]/5);
							g.setColor(oldColor);
						}
						else	//Draw a Clear texture if toggled
						{
							g.drawRect(objectSpecs[1][0], (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0)), objectSpecs[1][2], objectSpecs[1][3]/5);
						}
						if(iconToggle[i])	//If the icon is toggled
						{
							//Draw a blue ellipse
							Color oldColor = g.getColor();
							g.setColor(Scene.iconColor);
							g.fillOval(objectSpecs[1][0]-50, (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0)+objectSpecs[1][3]/15.0),
								objectSpecs[1][3]/18, objectSpecs[1][3]/18);
							g.setColor(oldColor);
						}
					}
					else	//Grays out image if not selected
					{
						Color oldColor = g.getColor();
						g.setColor(Scene.hoverColor);
						g.fillRect(objectSpecs[1][0]-1, (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0))-1, objectSpecs[1][2]+2, objectSpecs[1][3]/5+2);
						g.setColor(oldColor);
					}
					drawImages(g,i);
				}
			}
			private void drawImages(Graphics g, int i)	//Draws images for the icons
			{
				try {
					String fileName = "";
					switch(i)	//Draw icons
					{
						case 0:
						{
							fileName = "/Icon_Ult.png";
							break;
						}
						case 1:
						{
							fileName = "/Icon_Wild.png";
							break;
						}
						case 2:
						{
							fileName = "/Icon_Misere.png";
							break;
						}
						case 3:
						{
							fileName = "/Icon_Notakto.png";
							break;
						}
						case 4:
						{
							fileName = "/Icon_Random.png";
							break;
						}
					}
					g.drawImage(ImageIO.read(this.getClass().getResource(fileName)),
							objectSpecs[1][0]-1, (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0))-1, objectSpecs[1][2]+2, objectSpecs[1][3]/5+2,null);
				} catch (IOException e) {
					//Draws a rectangle if no image can be found
					g.fillRect(objectSpecs[1][0]-1, (int)(objectSpecs[1][1]+objectSpecs[1][3]*(i/5.0))-1, objectSpecs[1][2]+2, objectSpecs[1][3]/5+2);
				}
			}
			private void drawObjects(Graphics g)
			{
				//Title Text
				Font titleFont = Scene.defaultFont.deriveFont(64.0f).deriveFont(Font.BOLD);
				for(int i=0; i<objectSpecs.length;i++)
				{
					if(i ==0)	//Specifically, only the title has the largest font size and no border
					{
						titleFont = titleFont.deriveFont(64.0f);
					}
					else	//For every other button
					{
						titleFont = titleFont.deriveFont(48.0f);
						if((i == 3 && objectHovered[5])||(i == 4 && objectHovered[6]))	//Changes color if hovering over the back/start button
						{
							Color oldColor = g.getColor();
							g.setColor(Scene.hoverColor);
							g.fillRect(objectSpecs[i][0], objectSpecs[i][1], objectSpecs[i][2], objectSpecs[i][3]);
							g.setColor(oldColor);
						}
						else
							g.drawRect(objectSpecs[i][0], objectSpecs[i][1], objectSpecs[i][2], objectSpecs[i][3]);
					}
					//Draw Strings
					String name = objectNames[i];
					if(i == 2)	//Draws description differently
					{
						titleFont = titleFont.deriveFont(24f);
						name = desc[currentIcon+1][1];	//Replaces description with appropriate description
						this.drawString_MultiLiine(g,
								name,
								objectSpecs[i][0], 
								objectSpecs[i][1],
								objectSpecs[i][2],
								titleFont);
					}
					else	//Drawing other text
					{
						if(i == 0)	//Changes the title if an icon is selected
						{
							name = desc[currentIcon+1][0];	//Replaces title with icon title
						}
						this.drawString_Centered(g,
								name,
								objectSpecs[i][0], 
								objectSpecs[i][1],
								objectSpecs[i][2],
								objectSpecs[i][3],
								titleFont);
					}
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
			private void drawString_MultiLiine(Graphics g, String text, int x, int y, int width, Font font)	//Draws a multi-line String
			{
				FontMetrics fm = g.getFontMetrics(font);
			    g.setFont(font);
			    String temp [] = text.split("\n"); //Creates an array text split by \n
			    for(int i =0; i<temp.length;i++)
			    {
			    	if(fm.getStringBounds(temp[i], g).getWidth() > width)	//If the String is too wide, cut it up
			    	{
			    		String originalString = temp[i];
			    		String cutString = temp[i];
			    		while(fm.getStringBounds(cutString, g).getWidth() > width	//Cuts string until it fits inside the width
			    			|| (cutString.length() !=1 && !cutString.substring(cutString.length()-1).equals(" ")))	//And cut it off at a space to save words
			    		{
			    			cutString = cutString.substring(0,cutString.length()-1);
			    		}
			    		if(i == temp.length -1 )	//If at the end of the array
			    		{
			    			temp = new String[temp.length+1];
			    			temp[i+1] = "";
			    		}
			    		//Sets the string to only the cutString
			    		temp[i] = cutString;
			    		//Adding the cut string to the next line
			    		temp[i+1] = originalString.substring(cutString.length()) + temp[i+1];
			    	}
			    	y+= fm.getAscent()+fm.getDescent()+fm.getLeading();
			    	g.drawString(temp[i], x, y);
			    }
			}
		});

	}

}
