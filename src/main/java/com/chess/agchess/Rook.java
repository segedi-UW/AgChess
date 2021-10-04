package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public class Rook extends HasMovedPiece {
	
	private static final Image WHITE = new Image(Resolver.toURL("whiteRook.png"));
	private static final Image BLACK = new Image(Resolver.toURL("blackRook.png"));
	private static final int VALUE = 5;

	Rook(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
	}

	@Override
	public boolean canMove(Board board, Space end) {
		try {
			Line line = getLineTo(end);
			switch(line) {
			case LEFT:
			case UP:
			case DOWN:
			case RIGHT:
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
		Line[] lines = {Line.LEFT, Line.RIGHT, Line.UP, Line.DOWN};
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
