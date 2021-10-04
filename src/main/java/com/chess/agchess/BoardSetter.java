package com.chess.agchess;


public interface BoardSetter {
	void setup(Board board);
	int getRows();
	int getCols();
	King getWhiteKing();
	King getBlackKing();
}
