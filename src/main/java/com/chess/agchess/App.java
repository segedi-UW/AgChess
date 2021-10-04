package com.chess.agchess;

	
import java.net.URL;
import java.util.Hashtable;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
	
	private static final String TITLE = "AgChess";
	private ScreenController controller;
	
	@Override
	public void start(Stage primaryStage) {
		controller = new ScreenController(primaryStage);
		Hashtable<ScreenController.Screen, ScreenController.SceneBuilder> scenes = controller.getScenes();
		scenes.put(ScreenController.Screen.HOME, this::createHomeScene);
		scenes.put(ScreenController.Screen.GAME_SINGLE, this::createSingleGameScene);
		scenes.put(ScreenController.Screen.GAME_MULTI, this::createMultiGameScene);
		controller.setScene(ScreenController.Screen.HOME);
		primaryStage.setTitle(TITLE);
		primaryStage.show();
	}
	
	private Scene getDefaultScene(Parent p, double width, double height) {
		Scene scene = new Scene(p, width, height);
		URL url = getClass().getResource("application.css");
		if (url != null) {
			scene.getStylesheets().add(url.toExternalForm());
		} else {
			System.err.println("Could not find css file");
		}
		return scene;
	}
	
	private Scene createHomeScene(double width, double height) {
		BorderPane root = new BorderPane();
		Scene scene = getDefaultScene(root, width, height);
		
		final double buttonWidth = 300, buttonHeight = 70, opacity = 0.80;
		Button single = new Button("Local / Singleplayer");
		single.setOpacity(opacity);
		single.setMinSize(buttonWidth, buttonHeight);
		single.setOnAction(event -> controller.setScene(ScreenController.Screen.GAME_SINGLE));
		Button multi = new Button("Online Multiplayer");
		multi.setOpacity(opacity);
		multi.setMinSize(buttonWidth, buttonHeight);
		multi.setOnAction(event -> controller.setScene(ScreenController.Screen.GAME_MULTI));
		
		VBox buttons = new VBox(single, multi);
		buttons.setSpacing(30);
		buttons.setAlignment(Pos.BOTTOM_CENTER);
		
		Label title = new Label("Ag Chess");
		title.setId("title");
		DropShadow shadow = new DropShadow();
		double shadowSize = 50;
		shadow.setHeight(shadowSize);
		shadow.setWidth(shadowSize);
		title.setEffect(shadow);
		
		root.setCenter(buttons);
		root.setTop(title);
		BorderPane.setMargin(buttons, new Insets(0,0, height / 4,0));
		BorderPane.setAlignment(title, Pos.CENTER);
		URL url = getClass().getResource("agChessHome.jpg");
		if (url != null) {
			Background back = getBackground(new Image(url.toExternalForm()));
			root.setBackground(back);
		}
		return scene;
	}
	
	private Background getBackground(Image image) {
		BackgroundRepeat none = BackgroundRepeat.NO_REPEAT;
		BackgroundPosition position = BackgroundPosition.DEFAULT;
		final int width = ScreenController.APP_WIDTH_DEFAULT;
		final int height = ScreenController.APP_HEIGHT_DEFAULT;
		BackgroundSize size = new BackgroundSize(width, height, false, false, true, true);
		BackgroundImage backImage = new BackgroundImage(image, none, none, position, size);
		return new Background(backImage);
	}
	
	private Scene createSingleGameScene(double width, double height) {
		Board board = new Board(new DefaultBoard());
		SingleController game = new SingleController(board);
		return createGameScene(game, width, height);
	}
	
	private Scene createMultiGameScene(double width, double height) {
		Board board = new Board(new DefaultBoard());
		MultiController game = new MultiController(board, controller);
		return createGameScene(game, width, height);
	}
	
	private Scene createGameScene(Controller game, double width, double height) {
		BorderPane root = new BorderPane();
		Scene scene = getDefaultScene(root, width, height);
		root.setCenter(game.getDisplay());
		root.setBackground(getBackground(new Image(Resolver.toURL("silverBattle.jpg"))));
		Node leave = game.getExitControl(controller);
		root.setTop(leave);
		BorderPane.setAlignment(leave, Pos.TOP_RIGHT);
		root.setBottom(game.getControls());
		return scene;
	}
}
