package com.chess.agchess;


public class DefaultBoard implements BoardSetter {
	
	private static final int CELLS_ROW_COLUMN = 8;
	
	private final King whiteKing;
	private final King blackKing;

	public DefaultBoard() {
		whiteKing = new King(true);
		blackKing = new King(false);
	}
	
	@Override
	public void setup(Board board) {
		final boolean white = true;
		final boolean black = false;
		final int whitePawnRow = 6;
		final int blackPawnRow = 1;
		
		for (int r = 0; r < CELLS_ROW_COLUMN; r++) {
			for (int c = 0; c < CELLS_ROW_COLUMN; c++) {
				Space space = new Space(r, c);
				if (r == blackPawnRow) {
					board.setPiece(space, new Pawn(black));
				} else if (r == whitePawnRow) {
					board.setPiece(space, new Pawn(white));
				} else if (r == 0) {
					board.setPiece(space, getDefaultPowerPiece(black, c));
				} else if (r == 7) {
					board.setPiece(space, getDefaultPowerPiece(white, c));
				}
			}
		}
	}
	
	@Override
	public int getRows() {
		return CELLS_ROW_COLUMN;
	}
	
	@Override
	public int getCols() {
		return CELLS_ROW_COLUMN;
	}
	
	@Override
	public King getWhiteKing() {
		return whiteKing;
	}
	
	@Override
	public King getBlackKing() {
		return blackKing;
	}
	
	private Piece getDefaultPowerPiece(boolean isWhite, int col) {
		Piece piece;
		switch(col) {
		case 0:
		case 7:
			piece = new Rook(isWhite);
			break;
		case 1:
		case 6:
			piece = new Knight(isWhite);
			break;
		case 2:
		case 5:
			piece = new Bishop(isWhite);
			break;
		case 3:
			piece = new Queen(isWhite);
			break;
		case 4:
			piece = isWhite ? whiteKing : blackKing;
			break;
			default:
				throw new IllegalArgumentException("Not a default column (max index of 7): " + col);
		}
		return piece;
	}
}
