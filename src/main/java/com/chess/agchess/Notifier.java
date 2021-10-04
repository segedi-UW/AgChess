package com.chess.agchess;


import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Notifier implements Displayable {
	
	private final Label notificationDisplay;
	private final Label turnDisplay;
	private final Popup popup;
	private final TextArea popupArea;
	private final Remarker remarker;
	
	public enum Error {
		MOVE, CHECK, TURN
	}
	
	private static Label initializeLabel() {
		final double fontSize = 25.0;
		Label field = new Label();
		field.setFont(new Font(fontSize));
		field.setTextFill(new Color(1, 1, 1, 1));
		field.setAlignment(Pos.CENTER);
		return field;
	}
	
	private static TextArea initializePopupArea() {
		TextArea area = new TextArea();
		area.setMouseTransparent(true);
		final double opacity = 0.75;
		area.setOpacity(opacity);
		final boolean isEditable = false, doWrap = true;
		area.setEditable(isEditable);
		area.setWrapText(doWrap);
		final double width = 300.0, height = 42.0;
		area.setPrefSize(width, height);
		return area;
	}
	
	public Notifier() {
		notificationDisplay = initializeLabel();
		turnDisplay = initializeLabel();
		popup = new Popup();
		final boolean doAutoHide = true;
		popup.setAutoHide(doAutoHide);
		popupArea = initializePopupArea();
		popup.getContent().add(popupArea);
		remarker = new Remarker();
	}
	
	public void setText(String text) {
		notificationDisplay.setText(text);
	}
	
	public void setTurn(boolean isWhite) {
		turnDisplay.setText("It is " + (isWhite ? "White's" : "Black's") + " turn! ");
	}
	
	public void showMessage(String message) {
		popupArea.setText(message);
		popup.show(Stage.getWindows().get(0));
	}
	
	public void showError(Error error) {
		popupArea.setText(remarker.getRemark(Remarker.Type.ERROR));
		popup.show(Stage.getWindows().get(0));
	}
	
	@Override
	public Node getDisplay() {
		HBox hbox = new HBox(turnDisplay, notificationDisplay);
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	public void showCheckmate(boolean isLoserWhite) {
		String loser = isLoserWhite ? "White" : "Black";
		String winner = !isLoserWhite ? "White" : "Black";
		Alert alert = new Alert(AlertType.INFORMATION);
		ImageView winImage = new ImageView(new Image(winner.toLowerCase() + "King.png"));
		double size = 60;
		winImage.setFitHeight(size);
		winImage.setFitWidth(size);
		alert.setGraphic(winImage);
		alert.setTitle("Checkmate!");
		alert.setContentText("" + winner + "... " +
		remarker.getRemark(Remarker.Type.GOOD) + "\n" + loser + "... " + 
				remarker.getRemark(Remarker.Type.BAD));
		alert.setHeaderText("" + winner + " has put " + loser + " into checkmate!");
		Platform.runLater(alert::showAndWait);

	}
	
}
