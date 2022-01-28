// Mar 15 2021
package scenes;

import java.util.HashMap;

/*
 * This class allows for the accessing of scenes.
 */
public class SceneManager {
//VARIABLES
	
	public static HashMap<SceneKey,Scene> screenMap;	//A list of all the screen

//INITIALIZATION
	public static void init()	//Initializes all Scenes into the ArrayList
	{
		screenMap = new HashMap<SceneKey,Scene>();
		screenMap.put(SceneKey.MainMenu, new Scene_MainMenu());
		screenMap.put(SceneKey.Play, new Scene_Play());
		screenMap.put(SceneKey.Game, new Scene_Game());
	}
//METHODS
	public static void setScene(SceneKey sk)	//Given the SceneKey, this method will load the appropriate scene into Engine
	{
		screenMap.get(sk).loadScene();
	}
}
