package com.chess.agchess;


import javafx.scene.image.Image;

public abstract class HasMovedPiece extends Piece{

	private boolean hasMoved;

	HasMovedPiece(boolean isWhite, Image image, int value) {
		super(isWhite, image, value);
		hasMoved = false;
	}

	@Override
	public abstract boolean canMove(Board board, Space end);
	
	@Override
	protected Piece move(Board board, Space end) {
		hasMoved = true;
		return super.move(board, end);
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void resetHasMoved() {
		hasMoved = false;
	}
}
