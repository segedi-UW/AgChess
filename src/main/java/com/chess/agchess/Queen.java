package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public class Queen extends Piece {

	private static final Image WHITE = new Image(Resolver.toURL("whiteQueen.png"));
	private static final Image BLACK = new Image(Resolver.toURL("blackQueen.png"));
	private static final int VALUE = 9;
	
	Queen(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
	}

	@Override
	public boolean canMove(Board board, Space end) {
		try {
			getLineTo(end); // checks that a line is followed
			return isUnblocked(board, end) && canTakeOrMoveTo(board.getPiece(end));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public LinkedList<Space> getMoves(Board board) {
		LinkedList<Space> spaces = new LinkedList<>();
		Line[] lines = Line.values();
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
