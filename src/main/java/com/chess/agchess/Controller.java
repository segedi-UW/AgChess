package com.chess.agchess;


import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public abstract class Controller implements Board.ClickListener, Displayable, Controllable, Exits {

	private final Highlighter highlighter;
	private final Board board;
	private final Notifier notifier;
	private MediaPlayer clickSound;
	private final LinkedList<ChessListener> listeners;
	
	private boolean isWhiteTurn;
	private boolean isMuted;
	private Piece piece;
	
	public Controller(Board board) {
		this.board = board;
		isMuted = false;
		board.getListeners().add(this);
		highlighter = new Highlighter();
		isWhiteTurn = true;
		listeners = new LinkedList<>();
		notifier = new Notifier();
		notifier.setTurn(isWhiteTurn);
		try {
			URL url = getClass().getResource("moveClick.wav") ;
			if (url != null) {
				String source = url.toURI().toString();
				Media media = new Media(source);
				clickSound = new MediaPlayer(media);
				clickSound.setOnEndOfMedia(() -> {
					clickSound.stop();
					clickSound.seek(new Duration(0));
				});
			} else System.err.println("Could not load moveClick.wav as resource");
		} catch (URISyntaxException e) {
			System.out.println("Error obtaining Media source file: " + e.getMessage());
		}
	}
	
	@Override
	public void clickOn(Piece piece, Space space) {
		if (this.piece == null) {
			if (piece != null) {
				if (isTurn(piece.isWhite()))
					this.piece = piece;
				else
					notifier.showError(Notifier.Error.TURN);
			}
			if (this.piece != null ) {
				highlighter.highlightNode(board, this.piece.getSpace(), Highlighter.Highlight.SELECT);
				highlighter.highlightMoves(board, this.piece);
			}
		} else {
			movePiece(this.piece, space);
			this.piece = null;
			highlighter.clearHighlights(false);
		}
	}
	
	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}
	
	@Override
	public void cancel() {
		piece = null;
		highlighter.clearHighlights(false);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Notifier getNotifier() {
		return notifier;
	}
	
	public LinkedList<ChessListener> getListeners() {
		return listeners;
	}
	
	@Override
	public abstract Node getDisplay();
	
	/**
	 * Returns if the current turn is white or black
	 * @return true for white, false for black
	 */
	protected boolean isWhiteTurn() {
		return isWhiteTurn;
	}
	
	/**
	 * Called to check if the current color can attempt to move
	 * @param isWhite the color to move, true if white, false if black
	 * @return true if the color can move, false otherwise
	 */
	public abstract boolean isTurn(boolean isWhite);
	
	@Override
	public abstract Node getControls();
	
	@Override
	public abstract Node getExitControl(ScreenController controller);
	
	private void movePiece(Piece piece, Space end) {
		if (validateMove(piece, end)) {
			Piece captured = piece.move(board, end);
			playClick();
			if (captured != null) board.addCapture(captured);
			isWhiteTurn = !isWhiteTurn;
			notifier.setTurn(isWhiteTurn);
			nextTurn();
			King king = getCurrentKing();
			if (king.isInCheck(board)) {
				if (king.isCheckmate(board)) {
					checkmate(king);
				} else {
					check(king);
				}
			} else {
				notifier.setText("");
				highlighter.clearHighlights(true);
			}
		}
	}
	
	private void nextTurn() {
		listeners.forEach(ChessListener::nextTurn);
	}
	
	private void check(King king) {
		final boolean isWhite = king.isWhite();
		highlighter.highlightNodeIgnoreClear(board, king.getSpace(), Highlighter.Highlight.CHECK);
		notifier.setText("- " + (isWhite ? "White" : "Black") + " is in check!");
		listeners.forEach(listener -> listener.check(isWhite));
	}
	
	private void checkmate(King checkmated) {
		notifier.setText("- " + (checkmated.isWhite() ? "White" : "Black") + " is in checkmate!");
		King winner = board.getKing(!isWhiteTurn);
		highlighter.highlightNodeIgnoreClear(board, winner.getSpace(), Highlighter.Highlight.WIN);
		final boolean isCheckmatedWhite = checkmated.isWhite();
		getNotifier().showCheckmate(isCheckmatedWhite);
		listeners.forEach(listener -> listener.checkmate(isCheckmatedWhite));
	}
	
	private King getCurrentKing() {
		return board.getKing(isWhiteTurn);
	}
		
	private boolean validateMove(Piece piece, Space end) {
		boolean noCheck = piece.avoidedSelfCheck(board, end);
		boolean canMove = piece.canMove(board, end);
		if (!noCheck)
			notifier.showError(Notifier.Error.CHECK);
		else if (!canMove)
			notifier.showError(Notifier.Error.MOVE);
		return isTurn(piece.isWhite()) && canMove && noCheck ;
	}
	
	private void playClick() {
		if (clickSound != null && !isMuted) {
			clickSound.play();
		}
	}
}
