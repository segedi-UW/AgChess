package com.chess.agchess;


import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SingleController extends Controller implements ChessListener{
	
	private boolean autoFlip;

	public SingleController(Board board) {
		super(board);
		autoFlip = false;
		getListeners().add(this);
	}

	@Override
	public boolean isTurn(boolean isWhite) {
		return isWhiteTurn() == isWhite;
	}

	@Override
	public void nextTurn() {
		if (autoFlip)
			getBoard().flipBoard();
	}

	@Override
	public void checkmate(boolean isLoserWhite) {
	}

	@Override
	public void check(boolean isCheckedWhite) {
	}

	@Override
	public Node getDisplay() {
		return new VBox(getNotifier().getDisplay(), getBoard().getDisplay());
	}
	
	@Override
	public Node getControls() {
		return getControlPanel();
	}
	
	@Override
	public Node getExitControl(ScreenController controller) {
		Button leave = new Button("Leave Game");
		leave.setOnAction(event -> controller.setScene(ScreenController.Screen.HOME));
		return leave;
	}
	
	private Node getControlPanel() {
		Button flip = new Button("Flip Board");
		flip.setOnAction(event -> getBoard().flipBoard());
		CheckBox auto = new CheckBox("Auto Flip Board");
		auto.setOnAction(event -> autoFlip = auto.isSelected());
		CheckBox mute = new CheckBox("Mute Click");
		mute.setOnAction(event -> setMuted(mute.isSelected()));
		VBox checks = new VBox(auto, mute);
		HBox box = new HBox(flip, checks);
		box.setAlignment(Pos.BASELINE_RIGHT);
		box.setSpacing(5);
		return box;
	}
	
}
