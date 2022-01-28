// Mar. 18 2021
package scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Engine;
import window.Drawable;
import window.Screen;

/*
 * This class is where the actual game is played on
 */
public class Scene_Game extends Scene{
	/*
	 * rules
	 * An array of booleans displaying which rulesets are enabled.
	 * 	0 = Ultimate
	 * 	1 = Wild
	 * 	2 = Misere
	 *  3 = Notakto
	 *  4 = Random 
	 */
	public static boolean rules[] = new boolean[5]; 
	@Override
	public void screenInit() {
		screen = new Screen(new Drawable()
		{
			Dimension screenSize = Engine.engine.window.getSize();	//Screen Size
			
			int tick = 0;	//incrementor for animations
			int coinTick = 0; //incrementor value for coin toss frames
			int coinIncrementor = 1; //the value coinTick increments with
			int minCoinFlips = 3; //The coin must flip this amount of times before it can randomly stop
			int minCoinFlipsCounter = 0;//Counter for how many times the coin flipped
			int sceneTick = 0; //incrementor for when the scene resets after the game ends
			
			int lastMove = -1; //index of the last player's move
			boolean isFlipping = false; //when true, the coin is animated
			boolean isPlayer1Turn = true; //true is when Player 1 can move and false is when Player 2 can move
			
			String winner = "";	//stores the winner of the game
			
			/*	objectSpecs
			 * 	A 2D array to store each button's x, y, width, and height
			 * 	The first dimension stores the button and the second dimension stores its x,y, width, and height.
			 * 	0 0 = IconTray.x
			 * 	0 1 = IconTray.y
			 *  0 2 = IconTray.width
			 *  0 3 = IconTray.height
			 */
			int objectSpecs[][] = {
					{(int)(screenSize.width*.025),(int)(screenSize.height*.075),(int)(screenSize.width*.2),(int)(screenSize.width*.04)},	//IconTray
					{(int)(screenSize.width*.25),(int)(screenSize.height*.05),(int)(screenSize.width*.5),(int)(screenSize.width*.5)},	//Board
					{(int)(screenSize.width*.025),(int)(screenSize.height*.22),(int)(screenSize.width*.2),(int)(screenSize.width*.04)}, //Player 1
					{(int)(screenSize.width*.025),(int)(screenSize.height*.35),(int)(screenSize.width*.1),(int)(screenSize.width*.1)}, //Player 1 Options
					{(int)(screenSize.width*.775),(int)(screenSize.height*.22),(int)(screenSize.width*.2),(int)(screenSize.width*.04)}, //Player 2 
					{(int)(screenSize.width*.775),(int)(screenSize.height*.35),(int)(screenSize.width*.1),(int)(screenSize.width*.1)}, //Player 2 Options
					{(int)(screenSize.width*.08),(int)(screenSize.height*.7),(int)(screenSize.width*.1),(int)(screenSize.width*.1)}, //Coin Toss Indicator
					{(int)(0),(int)(0),(int)(screenSize.width),(int)(screenSize.height)},	//Victory Screen
				};
			
			/*
			 * optionHovered
			 * An array to store if each player's option's are hovered.
			 * Only used if Wild is enabled
			 * False means X is selected and True means O is selected
			 * 0 = Player 1 
			 * 1 = Player 2
			 */
			boolean optionSelected[] = {false,true};
			
			/*	objectHovered
			 * 	An array of booleans to determine if the mouse is over a specific toggle
			 * 	0 = optionSelected[0]
			 * 	1 = optionSelected[1]
			 *  2 to 82 = tic-tac-toe cells
			 	*  	2 - 10: represents the 3 x 3 board if ultimate is disabled
			 	*  	[2 ] [3 ] [4 ] | [11] [12] [13] | [20] [21] [22] 
					[5 ] [6 ] [7 ] | [14] [15] [16] | [23] [24] [25]  
					[8 ] [9 ] [10] | [17] [18] [19] | [26] [27] [28]  
					------------------------------------------------
					[29] [30] [31] | [38] [39] [40] | [47] [48] [49]  
					[32] [33] [34] | [41] [42] [43] | [50] [51] [52]  
					[35] [36] [37] | [44] [45] [46] | [53] [54] [55]
					------------------------------------------------  
					[56] [57] [58] | [65] [66] [67] | [74] [75] [76]  
					[59] [60] [61] | [68] [69] [70] | [77] [78] [79] 
					[62] [63] [64] | [71] [72] [73] | [80] [81] [82]  
			*/
			boolean objectHovered[] = new boolean [83];
			
			/*
			 * board
			 * An array of integers to store the tic-tac-toe board
			 	* 0 - 8 stores the 3 x 3 board if ultimate is disabled
			 	* 0 represents nothing, 1 represents X, -1 represents O
			 	[0 ] [1 ] [2 ] | [9 ] [10] [11] | [18] [19] [20] 
				[3 ] [4 ] [5 ] | [12] [13] [14] | [21] [22] [23]  
				[6 ] [7 ] [8 ] | [15] [16] [17] | [24] [25] [26]  
				[27] [28] [29] | [36] [37] [38] | [45] [46] [47]  
				[30] [31] [32] | [39] [40] [41] | [48] [49] [50]  
				[33] [34] [35] | [42] [43] [44] | [51] [52] [53]  
				[54] [55] [56] | [63] [64] [65] | [72] [73] [74]  
				[57] [58] [59] | [66] [67] [68] | [75] [76] [77]  
				[60] [61] [62] | [69] [70] [71] | [78] [79] [80]  
			 */
			int board[] = new int [81];
			
			/*
			 * ultimateBoard
			 * An array of integers to keep track of the winner of the smaller boards when ultimate is enabled
			 * 0 means nobody won, 1 means player 1 won, and -1 means player 2 won
			 */
			int ultimateBoard[] = new int[9];
			@Override
			public void update() {
				screenSize = Engine.engine.window.getSize();	//Reupdates the screenSize incase the window changes
				boundsCalc();
				clickFunctions();
				
				tick++;	//Increments tick every update 
				if(tick > 60)	//If tick surpasses 60, it resets to 0
				{
					tick = 0;
				}
				if(checkWin()[0] || checkWin()[1])	//if there is a winner, start the timer to switch scenes
				{
					sceneTick++;
					if(sceneTick >180) //After 3 seconds, the scene moves back to the main menu
					{
						SceneManager.setScene(SceneKey.MainMenu);
					}
				}
			}
			@Override
			public void draw(Graphics g) {
				//Background
				g.setColor(Scene.backgroundColor);
				g.fillRect(-10,-10,screenSize.width+10,screenSize.height+10);
				g.setColor(new Color(0,0,0));
				
				drawBoard(g);
				drawUltimateBoard(g);
				drawIconTray(g);
				
				drawPlayers(g);
				drawPlayerOptions(g);
				
				drawCoinToss(g);
				
				drawWinScreen(g);
				drawNextMoveIndicator(g);
			}
			//Assistant Functions
			private void boundsCalc()	//Updates various arrays if the mouse is in bounds
			{
				int mouseX = Engine.engine.mouseX;
				int mouseY = Engine.engine.mouseY;
				
				for(int i = 0; i< objectHovered.length;i++)	//Clears objectHovered
				{
					objectHovered[i] = false;
				}
				
				if(rules[1])	//if wild is enabled
				{
					for(int i = 3; i<= 5;i+=2)	//Check if the mouse is over the player options
					{
						int tempArray[] = objectSpecs[i].clone();
						tempArray[2] *= 2; //the width of objectSpecs is doubled in wild
						objectHovered[(i-3)/2] = inBounds(mouseX,mouseY,tempArray);
					}
				}
				if(!rules[0])	//if ultimate is disabled
				{
					//Each cell is 10/38 of the image width
					//The border is 1/38 of the image width
					//The first cell is 3/38 of the image width away from the top and left
					int tempArray [];
					for(int i = 0; i<3;i++)
					{
						for(int j = 0; j<3;j++)
						{
							tempArray = objectSpecs[1].clone();
							tempArray[0] = (int) (tempArray[0]+(3/38.0)*tempArray[2]+j*(11/38.0)*tempArray[2]);
							tempArray[1] = (int) (tempArray[1]+(3/38.0)*tempArray[2]+i*(11/38.0)*tempArray[2]);
							tempArray[2] = (int) (tempArray[2]*(10/38.0));
							tempArray[3] = (int) (tempArray[3]*(10/38.0));
							objectHovered[(2+3*i+j)] = inBounds(mouseX,mouseY,tempArray);
						}
					}
				}
				else	//if ultimate is enabled
				{
					//Each cell is 2/38 of the image width
					//The border is 1/38 of the image width
					//The first cell is 3/38 of the image width away from the top and left
					int tempArray [];
					for(int i = 0; i<81;i++)
					{
						int num = 0;

						tempArray = objectSpecs[1].clone();
						tempArray[0] = (int) (objectSpecs[1][0]+(4/38.0)*objectSpecs[1][2]+i%9*(3/38.0)*objectSpecs[1][2]+(i/3)%3*(2/38.0)*objectSpecs[1][2]);
						tempArray[1] = (int) (objectSpecs[1][1]+(4/38.0)*objectSpecs[1][2]+i/9*(3/38.0)*objectSpecs[1][2]+(2/38.0)*objectSpecs[1][2]*(i/27));
						tempArray[2] = (int) (objectSpecs[1][2]*(2/38.0));
						tempArray[3] = (int) (objectSpecs[1][3]*(2/38.0));
						num = i;
						//f(x) = x/3*9-x/3*3-x/9*24+x/27*18
						i = (i  +  (i/3)*9 - (i/3)*(3) -(i/9)*(24) + (i/27)*(18));	//Changes i into the modified format
						objectHovered[(2+i)] = inBounds(mouseX,mouseY,tempArray);
						i = num;
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
			private void clickFunctions()	//Determines the various responses when the left mouse button is clicked
			{
				if(Engine.engine.input1.isReleased(1))	//If the left mouse button is clicked
				{
					if(rules[1])	//if wild is enabled
					{
						for(int i = 0; i< optionSelected.length;i++)
						{
							if(objectHovered[i])	//If the mouse is hovered and pressed, toggle the selected option
							{
								optionSelected[i] = !optionSelected[i];
								break;
							}
						}
					}
					if(!rules[0])	//If ultimate is disabled
					{
						for(int i = 0;i <9;i++)
						{
							if(objectHovered[2+i])
							{
								moveCalc();
								break;
							}
						}
					}
					else
					{
						for(int i = 0;i <81;i++)
						{
							if(objectHovered[2+i])
							{
								moveCalc();
								break;
							}
						}
					}
				}
			}
			private void moveCalc()	//calculates what happens when a player tries to make a move
			{
				if(isFlipping || checkWin()[0] || checkWin()[1])	//You can't move while the coin is flipping or if somebody already won
				{
					return;
				}
				int boardSize = 3*3;
				if(rules[0])	//if ultimate is enabled
				{
					boardSize = 9*9;
				}
				for(int i = 0;i<boardSize;i++)
				{
					//Performing move
					if(objectHovered[2+i])	
					{
						if(board[i] != 0) //If the player tries to place a piece on an occupied square
						{
							return;
						}
						else if(rules[0] && lastMove != -1)	//else if ultimate is enabled and the move chosen is in the wrong square
						{
							/*	if lastMove % 9 == 0, then the top left corner is the only valid move
							 * 	1: top middle
							 * 	2: top right
							 * 	3: middle left
							 * 	4: middle middle
							 * 	5: middle right
							 * 	6: bottom left
							 * 	7: bottom middle
							 * 	8: bottom right
							 */
							//inverse of i  +  (i/3)*9 - (i/3)*(3) -(i/9)*(24) + (i/27)*(18) is itself
							if(!(9*(lastMove%9) <= i && 
								i <= 9*(lastMove%9+1)-1))	//If the move is played in the wrong board
							{
								//if the intended board is full, then the move is valid
								if(ultimateBoard[lastMove%9] == 0)
								{
									for(int j = 0; j<9;j++)
									{
										if(board[9*(lastMove%9)+j] == 0)
										{
											return;
										}
									}
								}
							}
							//But if the chosen board is already won, it can't be chosen either
							if(ultimateBoard[(i/9)] != 0)
							{
								return;
							}

						}
						if(isPlayer1Turn)	//If player 1's turn
						{
							if(!optionSelected[0]||rules[3])	//If X is selected
							{
								board[i] = 1;
							}
							else
							{
								board[i] = -1;
							}
						}
						else	//If Player 2's turn
						{
							if(!optionSelected[1]||rules[3])	//If X is selected
							{
								board[i] = 1;
							}
							else
							{
								board[i] = -1;
							}
						}
						if(rules[0])	//if ultimate is enabled
						{
							lastMove = (i);
						}
						else
						{
							lastMove = i;
						}
						if(rules[0])
						{
							updateUltimateBoard();
						}
						winCalc();
						changeTurns();
						return;
					}	
				}

			}
			private void updateUltimateBoard()	//Determines if there is a winner in a local board
			{
				for(int i = 0;i<9;i++)
				{
					for(int j = 0;j<3;j++)
					{
						if(ultimateBoard[i] != 0)	//if the board is already won, don't change it
						{
							continue;
						}
						int num = j*3;
						if((board[9*i+num] == board[9*i+num+1] && board[9*i+num] == board[9*i+num+2] && board[9*i+num] != 0) || //If all symbols match horizontally
								
							(board[9*i+j] == board[9*i+j+3] && board[9*i+j] == board[9*i+j+6] && board[9*i+j] != 0) || //If all symbols match vertically
							
							(((board[9*i] == board[9*i+4] && board[9*i] == board[9*i+8]) ||	//If all symbols match diagonally
							(board[9*i+2] == board[9*i+4] && board[9*i+2] == board[9*i+6])) && board[9*i+4] != 0))							
						{
							if(isPlayer1Turn||rules[3])
							{
								ultimateBoard[i] = 1;
								continue;
							}
							else
							{
								ultimateBoard[i] = -1;
								continue;
							}
						}
					}
				}
			}
			private void changeTurns()	//Changes turn order after a move is played
			{
				if(!rules[4])	//if random is disabled
				{
					isPlayer1Turn = !isPlayer1Turn;	//Swaps turn
				}
				else
				{
					isFlipping = true;
				}
			}
			private void winCalc()	//Determines what happens when a player wins
			{
				if(checkWin()[0] || checkWin()[1])	//if there is a winner
				{
					if(checkWin()[0] && checkWin()[1])
					{
						winner = "No one ";
					}
					else if(checkWin()[0])
					{
						winner = "Player 1 ";
					}
					else
					{
						winner = "Player 2 ";
					}
				}
			}
			private boolean tieCheck()	//Checks if the board is tied
			{
				int boardSize = 3*3;
				if(rules[0])	//if ultimate is enabled
				{
					for(int i = 0;i<9;i++)
					{
						if(ultimateBoard[i]==0)	//if there is still a board that is not won
						{
							return false;
						}
					}
				}
				for(int i = 0; i<boardSize;i++)
				{
					if(board[i] == 0)	//If there is still an empty square
					{
						return false;
					}
				}
				return true;
			}
			/*
			 * Checks the board to see if there is a winner
			 * False False = no winner
			 * False True = Player 1 Win
			 * True False = Player 2 Win
			 * True True = Tie
			 */
			private boolean[] checkWin()	
			{
				boolean winArray[] = new boolean[2];
				if(!rules[0]) //if ultimate is disabled
				{
					for(int i = 0; i<3;i++) 
					{
						int num = i*3;
						if((board[num] == board[num+1] && board[num] == board[num+2] && board[num] != 0) || //If all symbols match horizontally
								
							(board[i] == board[i+3] && board[i] == board[i+6] && board[i] != 0) || //If all symbols match vertically
							
							(((board[0] == board[4] && board[0] == board[8]) ||	//If all symbols match diagonally
							(board[2] == board[4] && board[2] == board[6])) && board[4] != 0))							
						{
							if(isPlayer1Turn && !rules[2])	//If it's player 1's turn and misere is disabled
							{
								winArray[0] = true;
							}
							else	//If it's player 2's turn or if player 1 won and misere was enabled
							{
								winArray[1] = true;
							}
						}
					}	
				}
				else	//if ultimate is enabled
				{
					for(int i = 0;i<3;i++)
					{
						int num =  i*3;
						if((ultimateBoard[num] == ultimateBoard[num+1] && ultimateBoard[num] == ultimateBoard[num+2] && ultimateBoard[num] != 0) || //If all symbols match horizontally
						
						(ultimateBoard[i] == ultimateBoard[i+3] && ultimateBoard[i] == ultimateBoard[i+6] && ultimateBoard[i] != 0) || //If all symbols match vertically
						
						(((ultimateBoard[0] == ultimateBoard[4] && ultimateBoard[0] == ultimateBoard[8]) ||	//If all symbols match diagonally
						(ultimateBoard[2] == ultimateBoard[4] && ultimateBoard[2] == ultimateBoard[6])) && ultimateBoard[4] != 0))							
						{
							if(isPlayer1Turn && !rules[2])	//If it's player 1's turn and misere is disabled
							{
								winArray[0] = true;
							}
							else	//If it's player 2's turn or if player 1 won and misere was enabled
							{
								winArray[1] = true;
							}
						}
					}
				}
				if(winArray[0] == winArray[1] && winArray[0] == false)
				{
					if(tieCheck())
					{
						winArray[0] = true;
						winArray[1] = true;
					}
				}
				return winArray;
			}
			private void drawPlayerOptions(Graphics g)	//Draw the X and O options available to each player
			{
				BufferedImage spriteSheet;
				BufferedImage xSymbol;
				BufferedImage oSymbol;
				try {
					spriteSheet = ImageIO.read(this.getClass().getResource("/Symbols.png"));
					xSymbol = spriteSheet.getSubimage(0, 0, 24, 24);
					oSymbol = spriteSheet.getSubimage(24,0,24,24);
					
					if(rules[1])	//If wild is enabled, where wild allows both X and O
					{
						Color oldColor = g.getColor();
						g.setColor(Scene.hoverColor);
						//Player 1
						if(!optionSelected[0])	//if false, select X
						{
							g.drawRect(objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
							g.fillRect(objectSpecs[3][0]+objectSpecs[3][2],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
						}
						else
						{
							g.fillRect(objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
							g.drawRect(objectSpecs[3][0]+objectSpecs[3][2],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
						}
						g.drawImage(xSymbol,objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3],null);
						g.drawImage(oSymbol,objectSpecs[3][0]+objectSpecs[3][2],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3],null);
						
						//Player 2
						if(!optionSelected[1])	//if false, select X
						{
							g.drawRect(objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
							g.fillRect(objectSpecs[5][0]+objectSpecs[5][2],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
						}
						else
						{
							g.fillRect(objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
							g.drawRect(objectSpecs[5][0]+objectSpecs[5][2],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
						}
						g.drawImage(xSymbol,objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3],null);
						g.drawImage(oSymbol,objectSpecs[5][0]+objectSpecs[5][2],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3],null);
						
						g.setColor(oldColor);
					}
					else if(rules[3])	//If notakto is enabled, where notakto only allows X
					{
						//Player 1
						g.drawRect(objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
						g.drawImage(xSymbol,objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3],null);
						
						//Player 2
						g.drawRect(objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
						g.drawImage(xSymbol,objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3],null);
					}
					else	//Otherwise, Player 1 has X and Player 2 has O
					{
						//Player 1
						g.drawRect(objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3]);
						g.drawImage(xSymbol,objectSpecs[3][0],objectSpecs[3][1],objectSpecs[3][2],objectSpecs[3][3],null);
						
						//Player 2
						g.drawRect(objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3]);
						g.drawImage(oSymbol,objectSpecs[5][0],objectSpecs[5][1],objectSpecs[5][2],objectSpecs[5][3],null);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			private void drawPlayers(Graphics g)	//Draws players and available moves
			{
				//Player 1 
				if(isPlayer1Turn)	//Clear rectangle
				{
					g.drawRect(objectSpecs[2][0], objectSpecs[2][1], objectSpecs[2][2], objectSpecs[2][3]);
				}
				else	//Shaded
				{
					Color oldColor = g.getColor();
					g.setColor(Scene.hoverColor);
					g.fillRect(objectSpecs[2][0], objectSpecs[2][1], objectSpecs[2][2], objectSpecs[2][3]);
					g.setColor(oldColor);
				}
				this.drawString_Centered(g, "Player 1", 
						objectSpecs[2][0], 
						objectSpecs[2][1], 
						objectSpecs[2][2], 
						objectSpecs[2][3], 
						Scene.defaultFont.deriveFont(32f));
				
				//Player 2
				if(isPlayer1Turn)	//Shaded
				{
					Color oldColor = g.getColor();

					g.setColor(Scene.hoverColor);
					g.fillRect(objectSpecs[4][0], objectSpecs[4][1], objectSpecs[4][2], objectSpecs[4][3]);
					g.setColor(oldColor);
				}
				else	//Clear rectangle
				{
					g.drawRect(objectSpecs[4][0], objectSpecs[4][1], objectSpecs[4][2], objectSpecs[4][3]);
				}
				this.drawString_Centered(g, "Player 2", 
						objectSpecs[4][0], 
						objectSpecs[4][1], 
						objectSpecs[4][2], 
						objectSpecs[4][3], 
						Scene.defaultFont.deriveFont(32f));
			}
			private void drawCoinToss(Graphics g)	//Draws the coin toss indicator
			{
				if(rules[4])	//if Random is enabled, the  indicator is drawn
				{			
					if(isFlipping)
					{
						if(tick%1 == 0 && tick != 0)
						{
							coinTick+=coinIncrementor;	//Increase coinTick every 2 frames
						}
						if(coinTick >11 || coinTick < 0)	//If coinTick is about to exceed the total number of frames, reverse directions
						{
							coinIncrementor = -coinIncrementor;
							coinTick += coinIncrementor;
						}
						if((coinTick == 11 ||coinTick == 0) && Math.random()<.3)	//10% of the time when the coin is showing heads or tails, it will stop
						{
							if(minCoinFlipsCounter >= minCoinFlips)	//The coin must flip at least 3 times before it can stop
							{
								isFlipping = false;
								if(coinTick == 11)	// If coin shows heads, it is player 1's turn
								{
									isPlayer1Turn = true;
								}
								else //If coin shows tails, it is player 2's turn
								{
									isPlayer1Turn = false;
								}
								minCoinFlipsCounter = 0;
							}
							else
							{
								minCoinFlipsCounter++;
							}
						}
					}
					drawCoinImage(g);
				}
				
			}
			private void drawCoinImage(Graphics g)	//Draws a frame from the the coin toss spritesheet
			{
				try {
					BufferedImage coinSheet = ImageIO.read(this.getClass().getResource("/CoinSheet.png"));	//Spritesheet of coin frames
					coinSheet = coinSheet.getSubimage(coinTick*32, 0, 32, 32);	//Get the specific frame from the image
					g.drawImage(coinSheet,objectSpecs[6][0], objectSpecs[6][1], objectSpecs[6][2], objectSpecs[6][3],null);	//draw coin frame
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			private void drawBoard(Graphics g)	//Draws the board
			{
				double gridSize = 3.0; //3 x 3 by default, 9 x 9 if ultimate is enabled
				if(rules[0])	//If ultimate is enabled
				{
					try		//Draws the Ultimate 9 x 9 board
					{	
						g.drawImage(ImageIO.read(this.getClass().getResource("/Icon_Ult.png")),
								objectSpecs[1][0],
								objectSpecs[1][1],
								objectSpecs[1][2],
								objectSpecs[1][3],
								null);
						BufferedImage spriteSheet = ImageIO.read(this.getClass().getResource("/Symbols.png"));
						BufferedImage xSymbol = spriteSheet.getSubimage(0, 0, 24, 24);
						BufferedImage oSymbol = spriteSheet.getSubimage(24,0,24,24);
						for(int i = 0; i<81;i++)	//Draw symbols onto board
						{
							if(board[i  +  (i/3)*9 - (i/3)*(3) -(i/9)*(24) + (i/27)*(18)] == 1)
							{
								g.drawImage(xSymbol,
								(int) (objectSpecs[1][0]+(4/38.0)*objectSpecs[1][2]+i%9*(3/38.0)*objectSpecs[1][2]+(i/3)%3*(2/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][1]+(4/38.0)*objectSpecs[1][2]+i/9*(3/38.0)*objectSpecs[1][2]+(2/38.0)*objectSpecs[1][2]*(i/27)),
								(int) (objectSpecs[1][2]*(2/38.0)),
								(int) (objectSpecs[1][3]*(2/38.0)),
								null);
							}
							else if(board[i  +  (i/3)*9 - (i/3)*(3) -(i/9)*(24) + (i/27)*(18)] == -1)
							{
								g.drawImage(oSymbol,
								(int) (objectSpecs[1][0]+(4/38.0)*objectSpecs[1][2]+i%9*(3/38.0)*objectSpecs[1][2]+(i/3)%3*(2/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][1]+(4/38.0)*objectSpecs[1][2]+i/9*(3/38.0)*objectSpecs[1][2]+(2/38.0)*objectSpecs[1][2]*(i/27)),
								(int) (objectSpecs[1][2]*(2/38.0)),
								(int) (objectSpecs[1][3]*(2/38.0)),
								null);
							}
						}
						return;
					} catch (IOException e) {
						gridSize = 9.0;
						e.printStackTrace();
					}
				}
				else	//If ultimate is not enabled
				{
					try		//Draws the default 3 x 3 board
					{	
						g.drawImage(ImageIO.read(this.getClass().getResource("/Icon_Default.png")),
								objectSpecs[1][0],
								objectSpecs[1][1],
								objectSpecs[1][2],
								objectSpecs[1][3],
								null);
						BufferedImage spriteSheet = ImageIO.read(this.getClass().getResource("/Symbols.png"));
						BufferedImage xSymbol = spriteSheet.getSubimage(0, 0, 24, 24);
						BufferedImage oSymbol = spriteSheet.getSubimage(24,0,24,24);
						for(int i = 0; i<9;i++)	//Draw symbols onto board
						{
							if(board[i] == 1)
							{
								g.drawImage(xSymbol,
								(int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][2]*(10/38.0)),
								(int) (objectSpecs[1][3]*(10/38.0)),
								null);
							}
							else if(board[i] == -1)
							{
								g.drawImage(oSymbol,
								(int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][2]*(10/38.0)),
								(int) (objectSpecs[1][3]*(10/38.0)),
								null);
							}
						}
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//If the image fails to load, this draws a default board
				for(int i = 0; i<gridSize;i++)
				{
					for(int j = 0; j<gridSize;j++)
					{
						g.drawRect(
						(int)(objectSpecs[1][0]+j*objectSpecs[1][2]/gridSize),
						(int)(objectSpecs[1][1]+i*objectSpecs[1][3]/gridSize),
						(int)(objectSpecs[1][2]/gridSize),
						(int)(objectSpecs[1][3]/gridSize));
					}
				}
				
			}
			private void drawUltimateBoard(Graphics g)	//Draws the big X and O if a small board is won
			{
				if(rules[0])	//If ultimate is enabled
				{
					BufferedImage spriteSheet;
					try {
						spriteSheet = ImageIO.read(this.getClass().getResource("/Symbols.png"));
						BufferedImage xSymbol = spriteSheet.getSubimage(0, 0, 24, 24);
						BufferedImage oSymbol = spriteSheet.getSubimage(24,0,24,24);
						
						for(int i = 0; i<9;i++)
						{
							if(ultimateBoard[i] != 0)
							{
								if(ultimateBoard[i] == 1)
								{
									g.drawImage(xSymbol,
									(int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
									(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
									(int) (objectSpecs[1][2]*(10/38.0)),
									(int) (objectSpecs[1][3]*(10/38.0)),
									null);
								}
								else
								{
									g.drawImage(oSymbol,
									(int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
									(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
									(int) (objectSpecs[1][2]*(10/38.0)),
									(int) (objectSpecs[1][3]*(10/38.0)),
									null);
								}
								Color tempColor = g.getColor();
								g.setColor(new Color(Scene.hoverColor.getRed(),Scene.hoverColor.getBlue(),Scene.hoverColor.getGreen(),122));
								g.fillRect((int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
								(int) (objectSpecs[1][2]*(10/38.0)),
								(int) (objectSpecs[1][3]*(10/38.0)));
								g.setColor(tempColor);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			private void drawIconTray(Graphics g)	//Draws the IconTray
			{
				for(int i =0;i<rules.length;i++)	//Draws the boundaries of the icontray
				{
					if(rules[i])	//If the icon is selected
					{
						g.drawRect((int)(objectSpecs[0][0]+objectSpecs[0][2]*(i/5.0)-1),
								objectSpecs[0][1]-1,
								(int)(objectSpecs[0][2]/5.0+2),
								objectSpecs[0][3]+2);
						
						Color oldColor = g.getColor();
						g.setColor(Scene.iconColor);
						//Active Rule Oval
						g.fillOval(objectSpecs[0][0]+(int)(objectSpecs[0][2]*(1+2*i)/10.0)-(int)(objectSpecs[0][2]/(5.0*3)/2.0),
							objectSpecs[0][1]-(int)(.6*objectSpecs[0][3]),
							(int)(objectSpecs[0][2]/(5.0*3)),
							(int)(objectSpecs[0][2]/(5.0*3)));
						g.setColor(oldColor);
					}
					else
					{
						//Draw a clear texture
						Color oldColor = g.getColor();
						g.setColor(Scene.hoverColor);
						g.fillRect((int)(objectSpecs[0][0]+objectSpecs[0][2]*(i/5.0)-1),
							objectSpecs[0][1]-1,
							(int)(objectSpecs[0][2]/5.0+2),
							objectSpecs[0][3]+2);
						//Inactive Rule Oval
						g.fillOval(objectSpecs[0][0]+(int)(objectSpecs[0][2]*(1+2*i)/10.0)-(int)(objectSpecs[0][2]/(5.0*3)/2.0),
								objectSpecs[0][1]-(int)(.6*objectSpecs[0][3]),
								(int)(objectSpecs[0][2]/(5.0*3)),
								(int)(objectSpecs[0][2]/(5.0*3)));
							g.setColor(oldColor);
						g.setColor(oldColor);
					}
					drawImages(g,i);
				}
			}
			private void drawWinScreen(Graphics g)
			{
				if(checkWin()[0] || checkWin()[1])	//If there is a winner
				{
					g.setColor(new Color(255,189,145,202));
					g.fillRect(objectSpecs[7][0], objectSpecs[7][1], objectSpecs[7][2], objectSpecs[7][3]);
					g.setColor(new Color(0,0,0,202));
					this.drawString_Centered(g, winner+ " is the winner!",
							objectSpecs[7][0],
							objectSpecs[7][1],
							objectSpecs[7][2],
							objectSpecs[7][3],
							Scene.defaultFont.deriveFont(48f)
							);
				}
			}
			private void drawNextMoveIndicator(Graphics g)	//If ultimate is enabled, this highlights the board the opponent can play a move in
			{
				if(checkWin()[0] || checkWin()[1])	//If there is a winner
				{
					return;
				}
				if(!rules[0])	//If ultimate is not enabled
				{
					return;
				}
				Color tempColor = g.getColor();
				g.setColor(new Color(Scene.hoverColor.getRed(),128,Scene.hoverColor.getGreen(),182));
				int moveIndex = -1;
				for(int i = 2;i<83;i++)	//Finds if a move is being hovered
				{
					if(objectHovered[i])
					{
						moveIndex = i-2;
						break;
					}
				}
				if(moveIndex== -1 && lastMove == -1)	//if there was no last move and nothing hovered
				{
					return;
				}
				for(int i = 0;i<9;i++)
				{
					if((i == moveIndex%9 && moveIndex != -1)||	//If there was not last move and i matches moveIndex, draw
						(i == lastMove%9 && moveIndex == -1)||	//If there was a last move and i matches last move, draw
						
						//If the chosen move is over a won board and i equal to the won board and a move is hovered and i doesn't equal a won board, draw
						(moveIndex != -1 && ultimateBoard[moveIndex%9] != 0 && i != moveIndex%9 && ultimateBoard[i] == 0)||	
						//If the last move is over a won board and i equal to the won board and a move isnt hovered and i doesn't equal a won board, draw
						(moveIndex == -1 && ultimateBoard[lastMove%9] != 0 && i != lastMove%9 && ultimateBoard[i] == 0))
					{
						g.fillRect((int) (objectSpecs[1][0]+(3/38.0)*objectSpecs[1][2]+i%3*(11/38.0)*objectSpecs[1][2]),
							(int) (objectSpecs[1][1]+(3/38.0)*objectSpecs[1][2]+i/3*(11/38.0)*objectSpecs[1][2]),
							(int) (objectSpecs[1][2]*(10/38.0)),
							(int) (objectSpecs[1][3]*(10/38.0)));
					}
				}
				g.setColor(tempColor);
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
						(int)(objectSpecs[0][0]+objectSpecs[0][2]*(i/5.0)-1),
						objectSpecs[0][1]-1,
						(int)(objectSpecs[0][2]/5.0+2),
						objectSpecs[0][3]+2,
						null);
				} catch (IOException e) {
					//Draws a rectangle if no image can be found
					g.fillRect((int)(objectSpecs[0][0]+objectSpecs[0][2]*(i/5.0)-1),
						objectSpecs[0][1]-1,
						(int)(objectSpecs[0][2]/5.0+2),
						objectSpecs[0][3]+2);
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
