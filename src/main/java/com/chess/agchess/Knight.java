package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public class Knight extends Piece {

	private static final Image BLACK = new Image(Resolver.toURL("blackKnight.png"));
	private static final Image WHITE = new Image(Resolver.toURL("whiteKnight.png"));
	private static final float VALUE = 3.5f;
	private static final int MOVE_A = 1;
	private static final int MOVE_B = 2;
			
	Knight(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
	}
	
	public static LinkedList<Space> getKnightSpaces(Board board, Space space) {
		LinkedList<Space> spaces = new LinkedList<>();
		Space[] set = calcKnightSpaces(space);
		final int maxRows = board.getMaxRows(), maxCols = board.getMaxCols();
		for (Space s : set) {
			if (isBetween(s.col, 0, maxCols - 1) && isBetween(s.row, 0, maxRows - 1))
				spaces.add(s);
		}
		return spaces;
	}
	
	private static Space[] calcKnightSpaces(Space space) {
		final int totalMoves = 8; // can only ever be 8 possible moves
		Space[] spaces = new Space[totalMoves];
		int[] modsX = {MOVE_A, -MOVE_A, MOVE_B, -MOVE_B, MOVE_A, -MOVE_A, MOVE_B, -MOVE_B};
		int[] modsY = {MOVE_B, MOVE_B, MOVE_A, MOVE_A,  -MOVE_B, -MOVE_B, -MOVE_A, -MOVE_A};
		for (int i = 0; i < spaces.length; i++) {
			spaces[i] = new Space(modsX[i] + space.row, modsY[i] + space.col);
		}
		return spaces;
	}

	@Override
	public boolean canMove(Board board, Space end) {
		Space space = getSpace();
		switch(absRow(space, end)) {
		case MOVE_A:
			return absCol(space, end) == MOVE_B && isUnblocked(board, end);
		case MOVE_B:
			return absCol(space, end) == MOVE_A && isUnblocked(board, end);
		}
		return false;
	}
	
	@Override
	public LinkedList<Space> getMoves(Board board) {
		LinkedList<Space> spaces = new LinkedList<>();
		LinkedList<Space> total = getKnightSpaces(board, getSpace());
		for (Space s : total) {
			if (canMove(board, s) && avoidedSelfCheck(board, s))
				spaces.add(s);
		}
		return spaces;
	}

	@Override
	protected boolean isUnblocked(Board board, Space end) {
		Piece target = board.getPiece(end);
		return target == null || canTake(target);
	}

}
