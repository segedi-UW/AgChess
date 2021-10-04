package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public class Pawn extends HasMovedPiece {
	
	private static final Image BLACK = new Image(Resolver.toURL("blackPawn.png"));
	private static final Image WHITE = new Image(Resolver.toURL("whitePawn.png"));
	private static final int VALUE = 1;

	private boolean wasFirstMove;
	private int moveNumber;
	private boolean isEnPassant;

	Pawn(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
		wasFirstMove = false;
	}

	@Override
	public boolean canMove(Board board, Space end) {
		isEnPassant = false;
		if (checkDirection(end)) {
			Space space = getSpace();
			int absCol = absCol(space, end);
			switch(absRow(space, end)) {
			case 1:
				switch(absCol) {
				case 0:
					return board.getPiece(end) == null;
				case 1:
					if (canTake(board.getPiece(end))) {
						return true;
					} 
					Pawn pawn = getEnPassantPawn(board, end);
					if (canEnPassant(pawn)) {
						isEnPassant = true;
						return true;
					}
				}
				break;
			case 2:
				return absCol == 0 && !hasMoved() && isUnblocked(board, end) && board.getPiece(end) == null;
			}
		}
		return false;
	}

	@Override
	public LinkedList<Space> getMoves(Board board) {
		LinkedList<Space> spaces = new LinkedList<>();
		int direction = getColorDirection();
		Space space = getSpace();
		int row1 = space.row + direction, row2 = space.row + (direction * 2);
		spaces.add(new Space(row1, space.col));
		spaces.add(new Space(row2, space.col));
		spaces.add(new Space(row1, space.col - 1));
		spaces.add(new Space(row1, space.col + 1));
		
		for (int i = 0; i < spaces.size(); i++) {
			Space s = spaces.get(i);
			if (!board.isValidSpace(s) || !canMove(board, s) || !avoidedSelfCheck(board, s)) {
				spaces.remove(i);
				i--;
			}
		}
		return spaces;
	}
	
	@Override
	protected Piece move(Board board, Space end) {
		wasFirstMove = !hasMoved();
		moveNumber = Piece.getMoves(isWhite());
		Piece taken = null;
		if (isEnPassant) {
			Pawn pawn = getEnPassantPawn(board, end);
			if (pawn != null)
				doEnPassant(board, pawn);
			taken = pawn;
		}
		Piece sTaken = super.move(board, end);
		if (sTaken != null) taken = sTaken;
		if (isLastRow(board, end))
			board.setPiece(end, new Queen(isWhite()));
		return taken;
	}
	
	private boolean isLastRow(Board board, Space end) {
		return isWhite() ? end.row == 0 : board.getMaxRows() - 1 == end.row;
	}
	
	private boolean canEnPassant(Pawn pawn) {
		return pawn != null && pawn.wasFirstMove && isSubsequentMove(pawn) && pawn.isWhite() != isWhite();
	}
	
	private boolean isSubsequentMove(Pawn pawn) {
		return pawn.moveNumber == (Piece.getMoves(pawn.isWhite()) - 1);
	}
	
	private void doEnPassant(Board board, Pawn pawn) {
		board.setPiece(pawn.getSpace(), null);
	}
	
	private Pawn getEnPassantPawn(Board board, Space end) {
		int mod = getColorDirection() * -1;
		Space space = new Space(end.row + mod, end.col);
		Piece target = board.getPiece(space);
		if (target instanceof Pawn)
			return(Pawn) target;
		return null;
	}
	
	private boolean checkDirection(Space end) {
		Space space = getSpace();
		return (isWhite() ? diffRow(space, end) < 0 : diffRow(space, end) > 0);
	}
	
	private int getColorDirection() {
		int BLACK_DIRECTION = 1;
		int WHITE_DIRECTION = -1;
		return isWhite() ? WHITE_DIRECTION : BLACK_DIRECTION;
	}
}
