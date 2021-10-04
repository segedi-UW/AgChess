package com.chess.agchess;


import java.util.Hashtable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenController {

	private final Stage primary;
	public static final int APP_WIDTH_DEFAULT = 800, APP_HEIGHT_DEFAULT = 600;
	private final Hashtable<Screen, SceneBuilder> scenes;
	private Scene current;
	
	public interface SceneBuilder {
		Scene buildScene(double width, double height);
	}
	
	public enum Screen {
		HOME, GAME_SINGLE, GAME_MULTI
	}

	public ScreenController(Stage primary) {
		this.primary = primary;
		scenes = new Hashtable<>();
	}
	
	public void setScene(Screen name) {
		Parent root = current != null ? current.getRoot() : null;
		double width = root != null ? root.getLayoutBounds().getWidth() : APP_WIDTH_DEFAULT;
		double height = root != null ? root.getLayoutBounds().getHeight() : APP_HEIGHT_DEFAULT;
		Scene scene = scenes.get(name).buildScene(width, height);
		if (scene != null) {
			primary.setScene(scene);
			current = scene;
		} else 
			throw new NullPointerException("Scene \"" + name.name() + "\" does not exist.");
	}
	
	public Hashtable<Screen, SceneBuilder> getScenes() {
		return scenes;
	}
	
}
