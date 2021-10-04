package com.chess.agchess;


public interface ChessListener {
	/**
	 * Called when the next turn begins. Can use this to set up communications on the
	 * beginning of a turn. 
	 */
	void nextTurn();
	/**
	 * Called when the color has lost
	 * @param isLoserWhite true if white lost, false if black lost
	 */
	void checkmate(boolean isLoserWhite);
	/**
	 * Called when the player had been put into check
	 * @param isCheckedWhite true if white is in check, false if black is in check
	 */
	void check(boolean isCheckedWhite);
	
}
