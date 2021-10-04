package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public class King extends HasMovedPiece {
	
	private static final Image BLACK = new Image(Resolver.toURL("blackKing.png"));
	private static final Image WHITE = new Image(Resolver.toURL("whiteKing.png"));
	private static final int VALUE = 0;
	
	private boolean isTesting;
	
	King(boolean isWhite) {
		super(isWhite, isWhite ? WHITE : BLACK, VALUE);
		isTesting = false;
	}

	@Override
	public boolean canMove(Board board, Space end) {
		// Castle
		// Regular move
		final Space space = getSpace();
		final int absRow = absRow(space, end), absCol = absCol(space, end);
		if (absRow <= 1 && absCol <= 1 && canTakeOrMoveTo(board.getPiece(end))) {
			// reg move
			return !isInCheck(board, end) && avoidedSelfCheck(board, end);
		} else if (isCastle(end) && !hasMoved()) {
			// check for castle stuff
			// - can't move through or into check
			try {
				Line line = getLineTo(end);
				Rook rook = getCastleRook(board, line);
				if (checkCastle(board, rook, line)) {
					return true;
				}
			} catch (IllegalArgumentException e) {
				// return false, but is redundant
			}
		}
		return false;
	}
	
	@Override
	public Piece move(Board board, Space end) {
		if (isCastle(end) && !isTesting) {
			Line line = getLineTo(end); // No need to check if line is valid if it could move
			Rook rook = getCastleRook(board, line);
			if (rook != null)
				doCastle(board, rook, line);
		}
		return super.move(board, end);
	}
	
	@Override
	public LinkedList<Space> getMoves(Board board) {
		LinkedList<Space> spaces = new LinkedList<>();
		int[] rows = {-1, 0, 1};
		int[] cols = {-1, 0, 1};
		Space space = getSpace();
		for (int row : rows) {
			for (int col : cols) {
				Space check = new Space(space.row + row, space.col + col);
				if (board.isValidSpace(check) && canMove(board, check) && avoidedSelfCheck(board, check))
					spaces.add(check);
			}
		}
		if (!hasMoved()) {
			Space left = new Space(space.row, space.col - 2);
			Space right = new Space(space.row, space.col + 2);
			if (canMove(board, left))
				spaces.add(left);
			if (canMove(board, right))
				spaces.add(right);
		}
		return spaces;
	}
	
	public boolean isInCheck(Board board) {
		return isInCheck(board, getSpace());
	}
	
	public boolean isInCheck(Board board, Space space) {
		LinkedList<Attack> attacks = getAttacks(board, space);
		return !attacks.isEmpty();
	}
	
	private LinkedList<Attack> getAttacks(Board board, Space space) {
		LinkedList<Attack> attacks = new LinkedList<>();
		Line[] lines = Line.values();
		// checking for reg pieces
		for (Line line : lines) {
			Attack attack = getNextAttackInLineBounded(board, line, space, null);
			if (attack != null && attack.attacker.isWhite() != isWhite() && attack.attacker.canMove(board, space)) {
				attacks.add(attack);
			}
		}
		// checking for knights
		LinkedList<Space> knightSpaces = Knight.getKnightSpaces(board, space);
		for (Space s : knightSpaces) {
			Piece p = board.getPiece(s);
			if (p instanceof Knight && p.isWhite() != isWhite())
				attacks.add(new Attack(p, new LinkedList<>()));
		}
		return attacks;
	}
	
	public boolean inCheckAfter(Board board, Piece piece, Space end) {
		isTesting = true;
		Space startSpace = piece.getSpace();
		boolean reset = piece instanceof HasMovedPiece && !((HasMovedPiece) piece).hasMoved();
		Piece endPiece = piece.move(board, end);
		if (reset)((HasMovedPiece) piece).resetHasMoved();
		boolean isInCheck = isInCheck(board);
		board.setPiece(end, null); // en pesant makes this necessary
		board.setPiece(endPiece != null ? endPiece.getSpace() : end, endPiece);
		board.setPiece(startSpace, piece);
		isTesting = false;
		if (isWhite()) MOVES_WHITE--; 
		else MOVES_BLACK--;
		return isInCheck;
	}
	
	public boolean isCheckmate(Board board) {
		// TODO 
		// check for attacking pieces to see if the king can move out of check
		// otherwise check for ways to block attacking pieces (perhaps calling isInCheck for each
		// blocking space in the attacker linked list
		// if there are multiple attackers, need to block a space that they both share, otherwise it cannot be done
		if (!getMoves(board).isEmpty()) return false;
		LinkedList<Attack> attacks = getAttacks(board, getSpace());
		LinkedList<Space> spacesToBlock = attacks.get(0).spacesToBlock; // need to block only one of these to prevent checkmate
		if (attacks.size() > 1) {
			for (int i = 1; i < attacks.size(); i++) {
				LinkedList<Space> compare = attacks.get(i).spacesToBlock;
				spacesToBlock = getSharedSpaces(spacesToBlock, compare);
			}
		}
		King enemy = board.getKing(!isWhite());
		for (Space s : spacesToBlock) {
			if (enemy.isInCheck(board, s)) return false;
		}
		return true; 
	}
	
	private LinkedList<Space> getSharedSpaces(LinkedList<Space> spaces1, LinkedList<Space> spaces2) {
		LinkedList<Space> shared = new LinkedList<>();
		for (Space s : spaces1) {
			if (spaces2.contains(s)) shared.add(s);
		}
		return shared;
	}
	
	private boolean isCastle(Space end) {
		Space space = getSpace();
		int absRow = absRow(space, end), absCol = absCol(space, end);
		return absCol == 2 && absRow == 0 && !hasMoved();
	}

	private Rook getCastleRook(Board board, Line line) {
		Piece piece = getNextPieceInLine(board, line);
		if (piece instanceof Rook && piece.isWhite() == isWhite()) {
			return (Rook) piece;
		}
		return null;
	}
	
	private boolean checkCastle(Board board, Rook rook, Line line) {
		Space space = getSpace();
		if (rook != null && !rook.hasMoved() && !isInCheck(board)) {
			int col = space.col + line.col;
			for (int i = 0; i < 2; i++) {
				Space checkSpace = new Space(space.row, col);
				if (isInCheck(board, checkSpace)) {
					return false;
				}
				col += line.col;
			}
			return true;
		}
		return false;
	}
	
	private void doCastle(Board board, Rook rook, Line line) {
		Space space = getSpace();
		rook.move(board, new Space(space.row, space.col + line.col));
	}
}