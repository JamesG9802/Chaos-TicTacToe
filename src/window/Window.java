//Jan. 26 2021
package window;

import javax.swing.JFrame;

@SuppressWarnings("serial")
/*
 * This class controls the Window that is shown on the screen
 */
public class Window extends JFrame{
//VARIABLES
	
	//Variable for Screen
	public Screen screen; 
	
//CONSTRUCTORS
	
	//Constructor with name and no writer
	public Window(String title)
	{
		super(title);
		init(null);
	}
	//Constructor with name and writer
	public Window(String title, Drawable writer)
	{
		super(title);
		init(writer);
	}
	//Constructor with no name and no writer
	public Window()
	{
		super();
		init(null);
	}
	//Constructor with no name and writer
	public Window(Drawable writer)
	{
		super();
		init(writer);
	}
//METHODS
	
	//Initialization Function
	private void init(Drawable writer)
	{
		//Initialize Screen
		screenInit(writer);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(screen);
		this.pack();
		this.setVisible(true);
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);		//Maximizes Screen
	}
	//Initialize Screen
	private void screenInit(Drawable writer)
	{
		//Check to see if Drawable was implemented
		if(writer == null)
		{
			writer = new Drawable() {public void update() {}public void draw(java.awt.Graphics g) {}};
		}
		screen = new Screen(writer);
		screen.setFocusable(true);
	}
//UPDATE AND RENDER
	
	public void update()
	{
		screen.writer.update();
	}
	public void draw()
	{
		screen.repaint();
	}	
//GETTERS AND SETTERS
	
	public void setWriter(Drawable writer)
	{
		screen.setWriter(writer);
	}
	public void setScreen(Screen s)	//Replaces old screen
	{
		this.remove(screen);
		s.setFocusable(true);
		s.setVisible(true);
		this.add(s);
		screen = s;
		revalidate();
	}
}
