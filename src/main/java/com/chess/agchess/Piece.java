package com.chess.agchess;


import java.util.LinkedList;

import javafx.scene.image.Image;

public abstract class Piece implements Comparable<Piece>{

	private static final Space EMPTY_SPACE = new Space(-1, -1);
	protected static int MOVES_WHITE = 0, MOVES_BLACK = 0;
	
	public static int getMoves(boolean isWhite) {
		return isWhite ? MOVES_WHITE : MOVES_BLACK;
	}
	
	protected static boolean isBetween(int num, int min, int max) {
		return num >= min && num <= max;
	}
	
	private final boolean isWhite;
	private final Image image;
	private final float value;
	private Space space;
	
	public enum Line {
		DOWN(0,1), UP(0,-1), LEFT(-1,0), RIGHT(1,0), 
		DIAGONAL_DOWN_LEFT(-1,1), DIAGONAL_DOWN_RIGHT(1,1),
		DIAGONAL_UP_LEFT(-1,-1), DIAGONAL_UP_RIGHT(1,-1);
		
		final int col, row;
		
		Line(int modCol, int modRow) {
			this.col = modCol;
			this.row = modRow;
		}
	}
	
	Piece(boolean isWhite, Image image, float value) {
		this.isWhite = isWhite;
		this.image = image;
		this.value = value;
		space = EMPTY_SPACE;
	}
	
	public boolean isWhite() {
		return isWhite;
	}
	
	public Image getImage() {
		return image;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
	
	public Space getSpace() {
		return space;
	}
	
	public float getValue() {
		return value;
	}
	
	public boolean avoidedSelfCheck(Board board, Space end) {
		return !board.getKing(isWhite()).inCheckAfter(board, this, end) && !board.getKing(!isWhite()).isInCheck(board);
	}
	
	@Override
	public String toString() {
		return (isWhite ? "White" : "Black") + " piece";
	}

	@Override
	public int compareTo(Piece p) {
		return Float.compare(p.value, value);
	}
	
	public abstract boolean canMove(Board board, Space end);
	
	public abstract LinkedList<Space> getMoves(Board board);
	
	protected Piece move(Board board, Space end) {
		board.setPiece(space, null);
		Piece toCapture = board.getPiece(end);
		board.setPiece(end, this);
		if (isWhite) MOVES_WHITE++; 
		else MOVES_BLACK++;
		return toCapture;
	}
	
	protected int absRow(Space space, Space end) {
		return Math.abs(diffRow(space, end));
	}
	
	protected int absCol(Space space, Space end) {
		return Math.abs(diffCol(space, end));
	}
	
	protected int diffRow(Space space, Space end) {
		return end.row - space.row;
	}
	
	protected int diffCol(Space space, Space end) {
		return end.col - space.col;
	}
	
	protected boolean isUnblocked(Board board, Space end) {
		try {
			Line line = getLineTo(end);
			Piece blockingPiece = getNextPieceInLineBounded(board, line, space, end);
			if (blockingPiece == null || (canTake(blockingPiece) && blockingPiece.space.equals(end)))
				return true;
		} catch (IllegalArgumentException e) {
			System.out.println("Not a proper line: " + space + ", " + end);
		}
		return false;
	}
	
	protected boolean canTake(Piece piece) {
		return piece != null && isWhite() != piece.isWhite();
	}
	
	protected boolean canTakeOrMoveTo(Piece piece) {
		return piece == null || canTake(piece);
	}
	
	protected Piece getNextPieceInLine(Board board, Line line) {
		return getNextPieceInLine(board, line, space);
	}
	
	protected Piece getNextPieceInLine(Board board, Line line, Space space) {
		return getNextPieceInLineBounded(board, line, space, null);
	}
	
	protected Piece getNextPieceInLineBounded(Board board, Line line, Space space, Space end) {
		Attack attack = getNextAttackInLineBounded(board, line, space, end);
		return attack != null ? attack.attacker : null;
	}
	
	protected Attack getNextAttackInLineBounded(Board board, Line line, Space space, Space end) {
		LinkedList<Space> spaces = getSpacesInLineBounded(board, line, space, end);
		int index = spaces.size() - 1;
		if (index < 0)
			return null;
		Space lastSpace = spaces.get(index);
		Piece p = board.getPiece(lastSpace);
		return p != null ? new Attack(p, spaces) : null;
	}
	
	protected LinkedList<Space> getSpacesInLineBounded(Board board, Line line, Space space, Space end) {
		final int maxRows = board.getMaxRows(), maxCols = board.getMaxCols();
		int r = space.row + line.row, c = space.col + line.col;
		Space check = new Space(r, c);
		LinkedList<Space> spaces = new LinkedList<>();
		while (isBetween(r, 0, maxRows - 1) && isBetween(c, 0, maxCols - 1) && (!check.equals(end))) {
			spaces.add(check);
			Piece p = board.getPiece(check);
			if (p != null)
				return spaces;
			r += line.row;
			c += line.col;
			check = new Space(r, c);
		}
		return spaces;
	}
	
	protected LinkedList<Space> getMoveLine(Board board, Line line) {
		return getSpacesInLineBounded(board, line, getSpace(), null);
	}
	
	protected Line getLineTo(Space end) throws IllegalArgumentException {
		int diffRow = diffRow(space, end), diffCol = diffCol(space, end);
		if (diffRow != 0 && diffCol != 0 && Math.abs((float)diffRow / (float)diffCol) != 1.0f)
			throw new IllegalArgumentException("Invalid line");
		int y = reduceTo1(diffRow), x = reduceTo1(diffCol);
		switch(x) {
		case -1:
			switch(y) {
			case 1:
				return Line.DIAGONAL_DOWN_LEFT;
			case 0:
				return Line.LEFT;
			case -1:
				return Line.DIAGONAL_UP_LEFT;
			default:
				throw new IllegalArgumentException("Invalid line");
			}
		case 0:
			switch(y) {
			case 1:
				return Line.DOWN;
			case -1:
				return Line.UP;
			default:
				throw new IllegalArgumentException("Invalid line");
			}
		case 1:
			switch(y) {
			case 1:
				return Line.DIAGONAL_DOWN_RIGHT;
			case 0:
				return Line.RIGHT;
			case -1:
				return Line.DIAGONAL_UP_RIGHT;
			default:
				throw new IllegalArgumentException("Invalid line");
			}
		default:
			throw new IllegalArgumentException("Invalid line");
		}
	}
	
	private int reduceTo1(int num) {
		if (num != 0)
			return num / Math.abs(num);
		return num;
	}
	
	public static class Attack {
		final public Piece attacker;
		final public LinkedList<Space> spacesToBlock;
		
		public Attack(Piece attacker, LinkedList<Space> spacesToBlock) {
			this.attacker = attacker;
			this.spacesToBlock = spacesToBlock;
		}
	}
}
