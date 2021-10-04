package com.chess.agchess;


import java.util.Comparator;
import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class Board implements Displayable {
	
	private static final int IMAGE_WIDTH = 40;
	private static final int IMAGE_HEIGHT = 40;
	private static final int REGION_WIDTH_HEIGHT = 50;
	private static final int CAPTURE_WIDTH_HEIGHT = 30;
	private static final double IMAGE_OPACITY = 1;
	private static final int BOARD_WIDTH = 400;
	private static final int BOARD_HEIGHT = 400;
	private static final int H_GAP = 0;
	private static final int V_GAP = H_GAP;
	
	private final int rows, cols;
	
	private final SpaceView[][] board;
	private final GridPane pane;
	private final ListView<ObservableList<Piece>> whiteCap;
	private final ListView<ObservableList<Piece>> blackCap;
	private final ObservableList<ObservableList<Piece>> whiteCaptures;
	private final ObservableList<ObservableList<Piece>> blackCaptures;
	private final HBox display;
	private final LinkedList<ClickListener> listeners;
	
	private final King blackKing;
	private final King whiteKing;
	
	public interface ClickListener {

		void clickOn(Piece piece, Space space);
		void cancel();
		
	}
	
	public interface CellWorker {
		void work(int row, int col);
	}
	
	private static class SpaceView {
		private final Space space;
		private Piece piece;
		private final ImageView image;
		
		public SpaceView(Space space) {
			this(space, null);
		}
		
		public SpaceView(Space space, Piece piece) {
			this.space = space;
			this.piece = piece;
			image = new ImageView();
			image.setOpacity(IMAGE_OPACITY);
			image.setFitWidth(IMAGE_WIDTH);
			image.setFitHeight(IMAGE_HEIGHT);
		}
		
		public void setPiece(Piece piece) {
			this.piece = piece;
			image.setImage( (piece != null ? piece.getImage() : null));
		}
		
		public Piece getPiece() {
			return piece;
		}
		
		public Space getSpace() {
			return space;
		}
	}
	
	public Board(BoardSetter setter) {
		rows = setter.getRows();
		cols = setter.getCols();
		whiteKing = setter.getWhiteKing();
		blackKing = setter.getBlackKing();
		board = new SpaceView[rows][cols];
		pane = initializeGridPane();
		StackPane boardPane = initializeDisplay(pane);
		blackCaptures = FXCollections.observableArrayList();
		whiteCaptures = FXCollections.observableArrayList();
		blackCap = createListView(blackCaptures);
		whiteCap = createListView(whiteCaptures);
		display = new HBox(blackCap, boardPane, whiteCap);
		display.setAlignment(Pos.CENTER);
		listeners = new LinkedList<>();
		setter.setup(this);
	}
	
	public void flipBoard() {
		int degree = (int) pane.getRotate() == 180 ? 0 : 180;
		pane.setRotate(degree);
		ObservableList<Node> nodes = pane.getChildren();
		for (Node node : nodes) {
			node.setRotate(degree);
		}
	}
	
	private static class CaptureCell extends ListCell<ObservableList<Piece>> {
		@Override protected void updateItem(ObservableList<Piece> pieces, boolean empty) {
	         super.updateItem(pieces, empty);
	         setBackground(null);
	         if (pieces != null) {
	        	 ImageView view = new ImageView(pieces.get(0).getImage());
		         view.setFitHeight(CAPTURE_WIDTH_HEIGHT);
		         view.setFitWidth(CAPTURE_WIDTH_HEIGHT);
	        	 setText("x " + pieces.size());
	        	 setGraphic(view);
	         } else {
	        	 setText(null);
	        	 setGraphic(null);
	         }
	     }
	}
	
	private ListView<ObservableList<Piece>> createListView(ObservableList<ObservableList<Piece>> list) {
		
		final int textBuffer = 40;
		ListView<ObservableList<Piece>> listView = new ListView<>(list);
		listView.setBackground(null);
		listView.setId("cell");
		listView.setMaxWidth(CAPTURE_WIDTH_HEIGHT + textBuffer);
		listView.setMinWidth(CAPTURE_WIDTH_HEIGHT + textBuffer);
		listView.setCellFactory(list1 -> new CaptureCell());
		return listView;
	}
	
	public void addCapture(Piece piece) {
		ObservableList<ObservableList<Piece>> lists = getCaptures(piece.isWhite());
		boolean added = false;
		for (ObservableList<Piece> list : lists) {
			if (list.get(0).getValue() == piece.getValue()) {
				list.add(piece);
				ListView<ObservableList<Piece>> view = piece.isWhite() ? whiteCap : blackCap;
				view.refresh();
				added = true;
				break;
			}
		}
		if (!added) {
			ObservableList<Piece> list = FXCollections.observableArrayList();
			list.add(piece);
			lists.add(list);
			Comparator<ObservableList<Piece>> listComparator = (l1, l2) -> {
				Piece p1 = l1.get(0);
				Piece p2 = l2.get(0);
				return p1.compareTo(p2);
			};
			lists.sort(listComparator);
		}
	}
	
	private ObservableList<ObservableList<Piece>> getCaptures(boolean isWhite) {
		return isWhite ? whiteCaptures : blackCaptures;
	}
	
	public void setPiece(Space space, Piece piece) {
		board[space.row][space.col].setPiece(piece);
		if (piece != null)
			piece.setSpace(space);
	}
	
	public Piece getPiece(Space space) {
		return isValidSpace(space) ? board[space.row][space.col].getPiece() : null;
	}
	
	public King getKing(boolean isWhite) {
		return isWhite ? whiteKing : blackKing;
	}
	
	public int getMaxRows() {
		return rows;
	}
	
	public int getMaxCols() {
		return cols;
	}
	
	@Override
	public HBox getDisplay() {
		return display;
	}
	
	public GridPane getPane() {
		return pane;
	}
	
	public LinkedList<ClickListener> getListeners() {
		return listeners;
	}
	
	public boolean isValidSpace(Space space) {
		return space.row < rows && space.col < cols && space.row >= 0 && space.col >= 0;
	}
	
	private GridPane initializeGridPane() {
		GridPane pane = new GridPane();
		pane.setSnapToPixel(true);
		setPaneSize(pane);
		forEachCell((row, col) -> {
			SpaceView view = new SpaceView(new Space(row, col));
			ImageView image = view.image;
			board[row][col] = view;
			Region region = new Region();
			image.setOnMousePressed(event -> notifyListeners(event, view));
			region.setOnMousePressed(event -> notifyListeners(event, view));
			region.setMinSize(REGION_WIDTH_HEIGHT, REGION_WIDTH_HEIGHT);
			pane.add(new StackPane(region, image), col, row);
		});
		return pane;
	}
	
	private void setPaneSize(GridPane pane) {
		pane.setHgap(H_GAP);
		pane.setVgap(V_GAP);
		pane.setMaxSize(REGION_WIDTH_HEIGHT * rows, REGION_WIDTH_HEIGHT * cols);
	}
	
	public void forEachCell(CellWorker worker) {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				worker.work(row, col);
			}
		}
	}
	
	private void notifyListeners(MouseEvent event, SpaceView view) {
		for (ClickListener listener : listeners) {
			if (event.isPrimaryButtonDown())
				listener.clickOn(view.getPiece(), view.getSpace());
			else
				listener.cancel();
		}
	}
	
	private StackPane initializeDisplay(GridPane pane) {
		StackPane display = new StackPane();
		display.setAlignment(Pos.CENTER);
		display.setPrefSize(BOARD_WIDTH, BOARD_HEIGHT);
		
		Image image = new Image(Resolver.toURL("chessBoardSilver.jpg"));
		ImageView view = new ImageView(image);
		final int borderWidth = 55;
		final int borderHeight = 54;
		final int widthHeight = REGION_WIDTH_HEIGHT * rows;
		view.setFitHeight(widthHeight + borderHeight);
		view.setFitWidth(widthHeight + borderWidth);
		
		display.getChildren().addAll(view, pane);
		StackPane.setAlignment(view, Pos.CENTER);
		StackPane.setAlignment(pane, Pos.CENTER);
		return display;
	}
}
