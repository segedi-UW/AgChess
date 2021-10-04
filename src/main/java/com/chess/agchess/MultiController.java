package com.chess.agchess;


import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MultiController extends Controller implements ChessListener{

	private final boolean isWhitePlayer; // TODO

	public MultiController(Board board, ScreenController controller) {
		super(board);
		getListeners().add(this);
		askHostOrJoin(controller);
		isWhitePlayer = determinePlayer();
	}
	
	private void askHostOrJoin(ScreenController controller) {
		Dialog<DialogBuilder.Address> dialog = DialogBuilder.buildMultiplayerDialog();
		Platform.runLater(() -> dialog.showAndWait().ifPresentOrElse(address -> {
			if (address.isHost) {
				// host game
				System.out.println("host");
			} else {
				// join game
				System.out.println("join");
			}
		}, () -> {
			// Return to Home screen
			System.out.println("canceled multiplayer");
			controller.setScene(ScreenController.Screen.HOME);
		}));
	}
	
	private boolean determinePlayer() {
		double d = Math.random();
		return d <= 0.5;
	}

	@Override
	public Node getDisplay() {
		return new VBox(getNotifier().getDisplay(), getBoard().getDisplay());
	}

	@Override
	public boolean isTurn(boolean isWhite) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void nextTurn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkmate(boolean isLoserWhite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void check(boolean isCheckedWhite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getControls() {
		return getControlPanel();
	}

	@Override
	public Node getExitControl(ScreenController controller) {
		Button leave = new Button("Leave Game");
		leave.setOnAction(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Are you sure you want to leave the game?");
			alert.setTitle("Exit Online Multiplayer Alert");
			alert.setHeaderText("Leave in progress game?");
			Platform.runLater(() -> alert.showAndWait().ifPresent(action -> {
				if (action == ButtonType.OK) {
					// TODO notify the connectionHandler that there was a disconnect
					controller.setScene(ScreenController.Screen.HOME);
				}
			}));
		});
		return leave;
	}
	
	private Node getControlPanel() {
		CheckBox mute = new CheckBox("Mute Click");
		mute.setOnAction(event -> setMuted(mute.isSelected()));
		HBox box = new HBox(mute);
		box.setAlignment(Pos.BASELINE_RIGHT);
		box.setSpacing(5);
		return box;
	}

}
