package com.chess.agchess;
import java.util.LinkedList;

import javafx.scene.image.Image;

public class Bishop extends Piece {

	private static final Image WHITE = new Image(Resolver.toURL("whiteBishop.png"));
	private static final Image BLACK = new Image(Resolver.toURL("blackBishop.png"));
	private static final int VALUE = 3;
	
	public Bishop(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
	}
	@Override
	public boolean canMove(Board board, Space end) {
		try {
			Line line = getLineTo(end);
			switch(line) {
			case DIAGONAL_DOWN_LEFT:
			case DIAGONAL_UP_LEFT:
			case DIAGONAL_DOWN_RIGHT:
			case DIAGONAL_UP_RIGHT:
				return isUnblocked(board, end) &&
						canTakeOrMoveTo(board.getPiece(end));
			default:
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	@Override
	public LinkedList<Space> getMoves(Board board) {
		LinkedList<Space> spaces = new LinkedList<>();
		Line[] lines = {Line.DIAGONAL_DOWN_LEFT, Line.DIAGONAL_UP_LEFT, Line.DIAGONAL_DOWN_RIGHT, Line.DIAGONAL_UP_RIGHT};
		for (Line line : lines) {
			LinkedList<Space> lineSpaces = getMoveLine(board, line);
			for (Space s : lineSpaces) {
				if (canMove(board, s) && avoidedSelfCheck(board, s)) {
					spaces.add(s);
				}
			}
		}
		return spaces;
	}

}
