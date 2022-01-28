package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import engine.Engine;
import scenes.SceneKey;
import scenes.SceneManager;

public class Main {

	public static void main(String a[])
	{
		SceneManager.init();	//Sets up the Scene Manager
		Engine.engine = new Engine("Template");
		Engine.engine.window.screen.setKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e) {
				SceneManager.setScene(SceneKey.MainMenu);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
}
