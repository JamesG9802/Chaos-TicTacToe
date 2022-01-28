//James G. - Jan 27 2021
package engine;
import java.awt.MouseInfo;

import window.Drawable;
import window.Window;
/*
 * This class initializes the Window object and runs the update/render loop
 */
public class Engine implements Runnable{
//VARIABLES
	public static Engine engine; //Allows engine to be accessed anywhere
	//Window Variables
	public Window window;
	
	//Thread Variables
	public boolean running; //Controls if the loop is running
	public Thread thread; 
	
	//Mouse Variables
	public int mouseX;	//relative x position compared to window
	public int mouseY;	//relative y position compared to window
	
	//Input Listeners
	public MouseInput input1; //stores mouse button input
	public KeyboardInput input2; //stores key input
//CONSTRUCTORS
	
	//Title only constructor
	public Engine(String title)
	{
		window = new Window(title);
		start();
	}
	//Writer only constructor
	public Engine(Drawable writer)
	{
		window = new Window(writer);
		start();
	}
	//Title and Writer constructor
	public Engine(String title, Drawable writer)
	{
		window = new Window(title, writer);
		start();
	}
//METHODS
	private void init()		//Initialization Method
	{
		input1 = new MouseInput(window);	//MouseInput
		input2 = new KeyboardInput(window);	//KeyboardInput
	}
//THREAD METHODS
	@Override
	public synchronized void run() {
		//Below is the code to control the amount of times the programs updates
				int fps = 60;
				double timePerTick = 1000000000/fps;
				double delta = 0;
				long now;
				long lastTime = System.nanoTime();
				long timer = 0;
				int ticks = 0;
				while(running)
				{
				
					now = System.nanoTime();
					delta += (now-lastTime) / timePerTick;
					timer+= now - lastTime;
					lastTime = now;
					if(delta >= 1) //Update loop
					{
						mouseX = (int)(MouseInfo.getPointerInfo().getLocation().getX() - (window.getX()+window.getInsets().right));
						mouseY = (int)(MouseInfo.getPointerInfo().getLocation().getY() - (window.getY()+window.getInsets().top));
						window.update(); //Update window
						
						input1.update(); //Update Inputs
						input2.update(); 

						window.draw();	//Render window
						ticks++;
						delta--;
						now = System.nanoTime();
					}
					if(timer >= 1000000000)
					{
						System.out.println("Ticks and Frames: "+ ticks);
						ticks = 0;
						timer = 0;
					}
				}
				stop();
	}
	private synchronized void start()	//Starts Thread Method
	{
		init();
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	private synchronized void stop()		//Halts Update Loop
	{
		running = false;
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

}
