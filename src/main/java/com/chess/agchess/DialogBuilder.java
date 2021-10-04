package com.chess.agchess;


import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class DialogBuilder {

	public static Dialog<Address> buildMultiplayerDialog() {
		Dialog<Address> dialog = new Dialog<>();
		dialog.setTitle("Host or Join Game");
		
		ButtonType connect = new ButtonType("Connect", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(connect, ButtonType.CANCEL);

		Label ipLabel = new Label("IP: ");
		Label portLabel = new Label("Port: ");
		TextAlignment text = TextAlignment.RIGHT;
		ipLabel.setTextAlignment(text);
		portLabel.setTextAlignment(text);
		TextField ip = new TextField("Ip Address");
		TextField port = new TextField("Port Number");
		
		GridPane grid = new GridPane();
		grid.addRow(0, ipLabel, ip);
		grid.addRow(1, portLabel, port);
		
		RadioButton host = new RadioButton("Host");
		host.setOnAction(event -> grid.setDisable(true));
		host.setSelected(true);
		grid.setDisable(true);
		RadioButton join = new RadioButton("Join");
		join.setOnAction(event -> grid.setDisable(false));
		ToggleGroup group = new ToggleGroup();
		group.getToggles().addAll(host, join);
		
		VBox content = new VBox(host, join, grid);

		dialog.getDialogPane().setContent(content);
		
		dialog.setResultConverter( button -> {
			System.out.println("ButtonType: " + button);
			if (button == ButtonType.OK) {
				try {
					int p = 0;
					if (!port.getText().isBlank())
						p = Integer.parseInt(port.getText());
					
					Address a = new Address();
					a.ip = ip.getText();
					a.port = p;
					a.isHost = host.isSelected();
					return a;
				} catch (NumberFormatException e) {
					System.out.println("Error: " + e.getMessage());
					return null;
				}
			} else {
				// cancel button
				return null;
			}
		});
		
		return dialog;
	}
	
	public static class Address {
		public int port;
		public String ip;
		public boolean isHost;
	}
}
