//Jan. 26 2021
package window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/*
 * This class controls the screen for drawing to the window.
 */
public class Screen extends JPanel{
//VARIABLES
	
	//Size Variables
	public int preferredWidth; 	//Variable for width
	public int preferredHeight;	//Variable for height
	
	//Interface Variables
	public Drawable writer; 		//Interfacce for drawing

//CONSTRUCTORS
	
	public Screen(Drawable writer)
	{
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//	preferredWidth = 250;
	//	preferredHeight = 200;
		preferredWidth = screenSize.width;
		preferredHeight = screenSize.height;
		this.writer = writer;
	}

//DRAWING METHOD
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		writer.draw(g);
	}
//GETTERS AND SETTERS
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredWidth, preferredHeight);
	}
	public void setWriter(Drawable writer)
	{
		this.writer = writer;
		this.repaint();
	}
	
	//Listeners
	
	public void setMouseListener(MouseListener mouseListener)	//resets all mouselisteners
	{
		if(getMouseListeners().length != 0)
		{
			for(MouseListener ml : getMouseListeners())
			{
				removeMouseListener(ml);
			}
		}
		addMouseListener(mouseListener);
	}
	public void setKeyListener(KeyListener keyListener)			//resets all keylisteners
	{
		if(getKeyListeners().length != 0)
		{
			for(KeyListener kl : getKeyListeners())
			{
				removeKeyListener(kl);
			}
		}
		addKeyListener(keyListener);
	}
}
