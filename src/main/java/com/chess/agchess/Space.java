package com.chess.agchess;


public class Space {
	
	private static final char ASCII_a = 'a';

	public final int row, col;
	
	public Space(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	@Override
	public String toString() {
		return "" + ((char) (col + ASCII_a)) + (row + 1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Space) {
			Space s = (Space) o;
			return s.row == row && s.col == col;
		}
		return false; 
	}
}
